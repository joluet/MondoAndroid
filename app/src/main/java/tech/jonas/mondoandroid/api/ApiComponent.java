package tech.jonas.mondoandroid.api;


import android.app.Application;

import com.f2prateek.rx.preferences.Preference;

import dagger.Component;
import tech.jonas.mondoandroid.api.authentication.AccessToken;
import tech.jonas.mondoandroid.api.authentication.OauthManager;
import tech.jonas.mondoandroid.api.authentication.RefreshToken;
import tech.jonas.mondoandroid.di.MondoComponent;
import tech.jonas.mondoandroid.di.scopes.ApiScope;
import tech.jonas.mondoandroid.features.home.HomeStringProvider;
import tech.jonas.mondoandroid.features.home.SubscriptionManager;
import tech.jonas.mondoandroid.gcm.GcmListenerService;
import tech.jonas.mondoandroid.utils.SchedulerProvider;

@ApiScope
@Component(dependencies = MondoComponent.class, modules = ApiModule.class)
public interface ApiComponent {

    void inject(GcmListenerService gcmListenerService);

    // Expose to subgraphs
    Application application();

    OauthManager oauthManager();

    MondoService mondoService();

    GcmService gcmService();

    HomeStringProvider stringProvider();

    @AccessToken
    Preference<String> accessToken();

    @RefreshToken
    Preference<String> refreshToken();

    SchedulerProvider schedulerProvider();

    SubscriptionManager subscriptionManager();
}
