package tech.jonas.mondoandroid.features.home;

import dagger.Module;
import dagger.Provides;
import tech.jonas.mondoandroid.api.MondoService;
import tech.jonas.mondoandroid.api.authentication.OauthManager;
import tech.jonas.mondoandroid.di.scopes.HomeScope;
import tech.jonas.mondoandroid.utils.SchedulerProvider;

@Module
public class HomeModule {
    private final HomeView homeView;

    public HomeModule(HomeView homeView) {
        this.homeView = homeView;
    }

    @Provides
    @HomeScope
    HomeView provideHomeView() {
        return homeView;
    }

    @Provides
    @HomeScope
    HomePresenter provideHomePresenter(HomeView view, HomeStringProvider stringProvider,
                                       SubscriptionManager subscriptionManager,
                                       OauthManager oauthManager, MondoService mondoService,
                                       SchedulerProvider schedulerProvider) {
        return HomePresenterImpl.builder()
                .withSubscriptionManager(subscriptionManager)
                .withStringProvider(stringProvider)
                .withView(view)
                .withOauthManager(oauthManager)
                .withMondoService(mondoService)
                .withSchedulerProvider(schedulerProvider)
                .build();
    }
}
