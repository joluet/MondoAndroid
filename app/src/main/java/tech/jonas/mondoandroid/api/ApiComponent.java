package tech.jonas.mondoandroid.api;


import android.app.Application;

import com.f2prateek.rx.preferences.Preference;

import dagger.Component;
import tech.jonas.mondoandroid.api.authentication.AccessToken;
import tech.jonas.mondoandroid.api.authentication.OauthManager;
import tech.jonas.mondoandroid.di.MondoComponent;
import tech.jonas.mondoandroid.di.scopes.ApiScope;
import tech.jonas.mondoandroid.features.home.HomeStringProvider;
import tech.jonas.mondoandroid.gcm.GcmListenerService;

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
}
