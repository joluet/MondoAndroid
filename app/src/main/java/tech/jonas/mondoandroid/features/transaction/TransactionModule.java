package tech.jonas.mondoandroid.features.transaction;

import com.f2prateek.rx.preferences.Preference;

import dagger.Module;
import dagger.Provides;
import tech.jonas.mondoandroid.api.MondoService;
import tech.jonas.mondoandroid.api.authentication.AccessToken;
import tech.jonas.mondoandroid.di.scopes.HomeScope;
import tech.jonas.mondoandroid.features.home.HomeStringProvider;
import tech.jonas.mondoandroid.features.home.SubscriptionManager;
import tech.jonas.mondoandroid.utils.SchedulerProvider;

@Module
public class TransactionModule {
    private final TransactionView transactionView;

    public TransactionModule(TransactionView transactionView) {
        this.transactionView = transactionView;
    }

    @Provides
    @HomeScope
    TransactionView provideTransactionView() {
        return transactionView;
    }

    @Provides
    @HomeScope
    TransactionPresenter provideTransactionPresenter(TransactionView view, HomeStringProvider stringProvider,
                                                     SubscriptionManager subscriptionManager,
                                                     MondoService mondoService,
                                                     @AccessToken Preference<String> accessToken,
                                                     SchedulerProvider schedulerProvider) {
        return TransactionPresenterImpl.builder()
                .withSubscriptionManager(subscriptionManager)
                .withStringProvider(stringProvider)
                .withView(view)
                .withMondoService(mondoService)
                .withSchedulerProvider(schedulerProvider)
                .build();
    }
}
