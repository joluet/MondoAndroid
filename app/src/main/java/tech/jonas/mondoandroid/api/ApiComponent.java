package tech.jonas.mondoandroid.api;


import com.f2prateek.rx.preferences.Preference;

import dagger.Component;
import tech.jonas.mondoandroid.api.authentication.AccessToken;
import tech.jonas.mondoandroid.api.authentication.OauthManager;
import tech.jonas.mondoandroid.di.MondoComponent;
import tech.jonas.mondoandroid.di.scopes.ApiScope;
import tech.jonas.mondoandroid.gcm.GcmListenerService;

@ApiScope
@Component(dependencies = MondoComponent.class, modules = ApiModule.class)
public interface ApiComponent {

    void inject(GcmListenerService gcmListenerService);

    // Expose to subgraphs
    OauthManager oauthManager();

    MondoService mondoService();

    GcmService gcmService();

    @AccessToken
    Preference<String> accessToken();
}
