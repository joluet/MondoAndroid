package tech.jonas.mondoandroid.features.transaction;

import tech.jonas.mondoandroid.api.MondoService;
import tech.jonas.mondoandroid.features.home.HomeStringProvider;
import tech.jonas.mondoandroid.features.home.SubscriptionManager;
import tech.jonas.mondoandroid.ui.model.BalanceMapper;
import tech.jonas.mondoandroid.ui.model.UiTransaction;
import tech.jonas.mondoandroid.utils.SchedulerProvider;

public class TransactionPresenterImpl implements TransactionPresenter {

    private final SubscriptionManager subscriptionManager;
    private final HomeStringProvider stringProvider;
    private final TransactionView view;
    private final MondoService mondoService;
    private final SchedulerProvider schedulerProvider;

    private TransactionPresenterImpl(Builder builder) {
        subscriptionManager = builder.subscriptionManager;
        stringProvider = builder.stringProvider;
        view = builder.view;
        mondoService = builder.mondoService;
        schedulerProvider = builder.schedulerProvider;
    }

    public static ISubscriptionManager builder() {
        return new Builder();
    }

    @Override
    public void onBindView(UiTransaction transaction) {
        if(transaction.hasSpending()) {
            String averageSpend = stringProvider.getFormattedGbp(transaction.spending.averageSpend / BalanceMapper.CENTS_PER_POUND);
            view.setAverageSpend(stringProvider.getAverageSpendText(averageSpend));
        }
        view.setAmount(transaction.formattedAmount);
        view.setMerchantName(transaction.merchantName);
        view.setLogoUrl(transaction.merchantLogo);
    }

    @Override
    public void onUnBindView() {

    }


    interface IBuild {
        TransactionPresenterImpl build();
    }

    interface ISchedulerProvider {
        IBuild withSchedulerProvider(SchedulerProvider val);
    }

    interface IMondoService {
        ISchedulerProvider withMondoService(MondoService val);
    }

    interface IView {
        IMondoService withView(TransactionView val);
    }

    interface IStringProvider {
        IView withStringProvider(HomeStringProvider val);
    }

    interface ISubscriptionManager {
        IStringProvider withSubscriptionManager(SubscriptionManager val);
    }

    public static final class Builder implements ISchedulerProvider, IMondoService, IView, IStringProvider, ISubscriptionManager, IBuild {
        private SchedulerProvider schedulerProvider;
        private MondoService mondoService;
        private TransactionView view;
        private HomeStringProvider stringProvider;
        private SubscriptionManager subscriptionManager;

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
        public IMondoService withView(TransactionView val) {
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

        public TransactionPresenterImpl build() {
            return new TransactionPresenterImpl(this);
        }
    }
}
