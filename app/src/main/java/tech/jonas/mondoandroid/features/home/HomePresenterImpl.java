package tech.jonas.mondoandroid.features.home;

import android.net.Uri;
import android.support.v4.util.Pair;
import android.util.Log;

import java.util.Collections;
import java.util.Map;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscription;
import tech.jonas.mondoandroid.api.Config;
import tech.jonas.mondoandroid.api.MondoService;
import tech.jonas.mondoandroid.api.authentication.OauthManager;
import tech.jonas.mondoandroid.api.model.Account;
import tech.jonas.mondoandroid.api.model.Merchant;
import tech.jonas.mondoandroid.api.model.Transaction;
import tech.jonas.mondoandroid.api.model.TransactionList;
import tech.jonas.mondoandroid.ui.model.BalanceMapper;
import tech.jonas.mondoandroid.ui.model.Spending;
import tech.jonas.mondoandroid.ui.model.TransactionMapper;
import tech.jonas.mondoandroid.utils.ListUtils;
import tech.jonas.mondoandroid.utils.RxUtils;
import tech.jonas.mondoandroid.utils.SchedulerProvider;

public class HomePresenterImpl implements HomePresenter {

    private final SubscriptionManager subscriptionManager;
    private final HomeStringProvider stringProvider;
    private final HomeView view;
    private final OauthManager oauthManager;
    private final MondoService mondoService;
    private final SchedulerProvider schedulerProvider;

    private HomePresenterImpl(Builder builder) {
        subscriptionManager = builder.subscriptionManager;
        stringProvider = builder.stringProvider;
        view = builder.view;
        oauthManager = builder.oauthManager;
        mondoService = builder.mondoService;
        schedulerProvider = builder.schedulerProvider;
    }

    public static ISubscriptionManager builder() {
        return new Builder();
    }

    @Override
    public void onBindView(Uri uri) {
        view.setIsLoading(true);
        if (!oauthManager.isAuthenticated() && uri == null) {
            view.startLoginActivity();
        } else if (uri != null && "mondo.co.uk".equals(uri.getHost())) {
            final Observable<String> tokenObservable = oauthManager.getAuthToken(uri).cache();

            // Obtain auth token
            Subscription tokenSub = tokenObservable.compose(schedulerProvider.getSchedulers())
                    .subscribe(token -> {
                        getTransactionsAndUpdateUI();
                    }, throwable -> RxUtils.crashOnError());
            subscriptionManager.add(tokenSub);

            // Register webhook for notifications
            Subscription webhookSub = tokenObservable.flatMap(token ->
                    oauthManager.registerWebhook()).compose(schedulerProvider.getSchedulers())
                    .subscribe(webhook -> {
                    }, throwable -> RxUtils.crashOnError());
            subscriptionManager.add(webhookSub);
        } else {
            getTransactionsAndUpdateUI();
        }
    }

    @Override
    public void onUnBindView() {
        subscriptionManager.unsubscribe();
    }

    @Override
    public void onRefresh() {
        view.setIsLoading(true);
        getTransactionsAndUpdateUI();
    }

    @Override
    public void onLogoutClicked() {
        oauthManager.logout();
        view.startLoginActivity();
    }

    private void refreshTokenAndUpdateUI() {
        final Observable<String> tokenObservable = oauthManager.refreshAuthToken().cache();

        Subscription tokenSub = tokenObservable
                .compose(schedulerProvider.getSchedulers())
                .subscribe(token -> {
                    getTransactionsAndUpdateUI();
                }, throwable -> {
                    if (throwable instanceof HttpException) {
                        view.startLoginActivity();
                    } else {
                        RxUtils.crashOnError();
                    }
                });
        subscriptionManager.add(tokenSub);

        Subscription webhookSub = tokenObservable.flatMap(token ->
                oauthManager.registerWebhook()).compose(schedulerProvider.getSchedulers())
                .subscribe(webhook -> {
                }, throwable -> RxUtils.crashOnError());
        subscriptionManager.add(webhookSub);
    }

    private void getTransactionsAndUpdateUI() {
        Observable<Account> accountObservable = mondoService.getAccounts()
                .map(accounts -> ListUtils.first(accounts.accounts)).cache();

        Subscription balanceSub = accountObservable
                .flatMap(account -> mondoService.getBalance(account.id))
                .compose(schedulerProvider.getSchedulers())
                .compose(BalanceMapper.map(stringProvider))
                .subscribe(balance -> {
                    view.setTitle(stringProvider.getFormattedBalance(balance.formattedAmount));
                }, throwable -> {
                    if (throwable instanceof HttpException) {
                        refreshTokenAndUpdateUI();
                    } else {
                        RxUtils.crashOnError();
                    }
                });
        subscriptionManager.add(balanceSub);

        Observable<TransactionList> transactionObservable = accountObservable
                .flatMap(account -> mondoService.getTransactions(account.id, "merchant")).cache();

        Subscription transactionSub = calculateSpendingPerMerchant(transactionObservable)
                .flatMap(spendingPerMerchant -> transactionObservable
                        .doOnCompleted(() -> view.setIsLoading(false))
                        .compose(TransactionMapper.map(stringProvider, spendingPerMerchant))
                        .map(transactionsToDisplay -> {
                            Collections.reverse(transactionsToDisplay);
                            return transactionsToDisplay;
                        }))
                .compose(schedulerProvider.getSchedulers())
                .subscribe(view::setTransactions, throwable -> {
                    if (throwable instanceof HttpException) {
                        refreshTokenAndUpdateUI();
                    } else {
                        RxUtils.crashOnError();
                    }
                });
        subscriptionManager.add(transactionSub);
    }

    private Observable<Map<Merchant, Spending>> calculateSpendingPerMerchant(Observable<TransactionList> transactionObservable) {
        Observable<Transaction> validTransactions = transactionObservable
                .map(transactionList -> transactionList.transactions)
                .flatMap(Observable::from)
                .filter(transaction -> transaction.declineReason == null)
                .filter(transaction -> transaction.merchant != null);

        Observable<Merchant> merchants = validTransactions
                .map(transaction -> transaction.merchant)
                .distinct(merchant -> merchant.id);

        return merchants.flatMap(merchant -> validTransactions
                .filter(transaction -> merchant.id.equals(transaction.merchant.id))
                .toList()
                .map(transactions -> {
                    long total = 0;
                    for (Transaction transaction : transactions) {
                        total += Math.abs(transaction.amount);
                    }
                    int count = transactions.size();
                    long average = total / count;
                    return Pair.<Merchant,Spending>create(merchant, new Spending(total, average, count));
                }))
                .toList()
                .map(ListUtils::toMap);
    }

    interface IBuild {
        HomePresenterImpl build();
    }

    interface ISchedulerProvider {
        IBuild withSchedulerProvider(SchedulerProvider val);
    }

    interface IMondoService {
        ISchedulerProvider withMondoService(MondoService val);
    }

    interface IOauthManager {
        IMondoService withOauthManager(OauthManager val);
    }

    interface IView {
        IOauthManager withView(HomeView val);
    }

    interface IStringProvider {
        IView withStringProvider(HomeStringProvider val);
    }

    interface ISubscriptionManager {
        IStringProvider withSubscriptionManager(SubscriptionManager val);
    }

    public static final class Builder implements ISchedulerProvider, IMondoService, IOauthManager, IView, IStringProvider, ISubscriptionManager, IBuild {
        private MondoService mondoService;
        private OauthManager oauthManager;
        private HomeView view;
        private HomeStringProvider stringProvider;
        private SubscriptionManager subscriptionManager;
        private SchedulerProvider schedulerProvider;

        private Builder() {
        }

        @Override
        public IBuild withSchedulerProvider(SchedulerProvider val) {
            schedulerProvider = val;
            return this;
        }

        @Override
        public ISchedulerProvider withMondoService(MondoService val) {
            mondoService = val;
            return this;
        }

        @Override
        public IMondoService withOauthManager(OauthManager val) {
            oauthManager = val;
            return this;
        }

        @Override
        public IOauthManager withView(HomeView val) {
            view = val;
            return this;
        }

        @Override
        public IView withStringProvider(HomeStringProvider val) {
            stringProvider = val;
            return this;
        }

        @Override
        public IStringProvider withSubscriptionManager(SubscriptionManager val) {
            subscriptionManager = val;
            return this;
        }

        public HomePresenterImpl build() {
            return new HomePresenterImpl(this);
        }
    }
}
