package tech.jonas.mondoandroid.features.home;

import android.net.Uri;

import com.f2prateek.rx.preferences.Preference;

import java.util.Collections;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscription;
import tech.jonas.mondoandroid.api.Config;
import tech.jonas.mondoandroid.api.MondoService;
import tech.jonas.mondoandroid.api.authentication.OauthManager;
import tech.jonas.mondoandroid.ui.model.BalanceMapper;
import tech.jonas.mondoandroid.ui.model.TransactionMapper;
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

    private void refreshTokenAndUpdateUI() {
        Subscription tokenSub = oauthManager.refreshAuthToken()
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
    }

    private void getTransactionsAndUpdateUI() {
        Subscription balanceSub = mondoService.getBalance(Config.ACCOUNT_ID)
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

        Subscription transactionSub = mondoService.getTransactions(Config.ACCOUNT_ID, "merchant")
                .compose(schedulerProvider.getSchedulers())
                .doOnCompleted(() -> view.setIsLoading(false))
                .compose(TransactionMapper.map(stringProvider))
                .map(transactions -> {
                    Collections.reverse(transactions);
                    return transactions;
                })
                .subscribe(view::setTransactions,
                        throwable -> {
                            if (throwable instanceof HttpException) {
                                refreshTokenAndUpdateUI();
                            } else {
                                RxUtils.crashOnError();
                            }
                        });
        subscriptionManager.add(transactionSub);
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
        private Preference<String> accessToken;
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
