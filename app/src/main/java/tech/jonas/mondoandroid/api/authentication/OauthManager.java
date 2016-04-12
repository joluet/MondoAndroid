package tech.jonas.mondoandroid.api.authentication;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;

import com.f2prateek.rx.preferences.Preference;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import okhttp3.HttpUrl;
import rx.Observable;
import tech.jonas.mondoandroid.R;
import tech.jonas.mondoandroid.api.Config;
import tech.jonas.mondoandroid.api.GcmService;
import tech.jonas.mondoandroid.api.MondoService;
import tech.jonas.mondoandroid.api.model.RegistrationToken;
import tech.jonas.mondoandroid.api.model.Webhook;
import tech.jonas.mondoandroid.data.IntentFactory;
import tech.jonas.mondoandroid.di.scopes.ApiScope;
import tech.jonas.mondoandroid.utils.Util;

@ApiScope
public class OauthManager {
    private final IntentFactory intentFactory;
    private final Preference<String> accessToken;
    private final Application application;
    private final MondoService mondoService;
    private final GcmService gcmService;
    private String randomId;

    @Inject
    public OauthManager(IntentFactory intentFactory, MondoService mondoService, GcmService gcmService,
                        @AccessToken Preference<String> accessToken, Application application) {
        this.intentFactory = intentFactory;
        this.mondoService = mondoService;
        this.gcmService = gcmService;
        this.accessToken = accessToken;
        this.application = application;
    }

    public Intent createLoginIntent() {
        randomId = UUID.randomUUID().toString();
        HttpUrl authorizeUrl = HttpUrl.parse("https://auth.getmondo.co.uk")
                .newBuilder()
                .addQueryParameter("client_id", Config.CLIENT_ID)
                .addQueryParameter("redirect_uri", "https://mondo.co.uk")
                .addQueryParameter("response_type", "code")
                .addQueryParameter("state", randomId)
                .build();

        return intentFactory.createUrlIntent(authorizeUrl.toString());
    }

    public Observable<String> getAuthToken(Uri data) {
        final Map<String, String> params = new HashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("client_id", Config.CLIENT_ID);
        params.put("client_secret", Config.CLIENT_SECRET);
        params.put("redirect_uri", "https://mondo.co.uk");
        params.put("code", data.getQueryParameter("code"));
        return mondoService.getAccessToken(params)
                .map(accessTokenResponse -> {
                    accessToken.set(accessTokenResponse.accessToken);
                    return accessTokenResponse.accessToken;
                });
    }

    public Observable<Webhook> registerWebhook() {
        return mondoService.getAccounts().flatMap(accounts ->
                Observable.<RegistrationToken>create(subscriber -> {
                    InstanceID instanceID = InstanceID.getInstance(application);
                    try {
                        final String token = instanceID.getToken(application.getString(R.string.gcm_defaultSenderId),
                                GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(new RegistrationToken(Util.first(accounts.accounts).id, token));
                            subscriber.onCompleted();
                        }
                    } catch (IOException e) {
                        subscriber.onError(e);
                    }
                })).flatMap(registrationToken -> gcmService.uploadToken(registrationToken)
                .flatMap(registrationAnswer -> mondoService.registerWebhook(registrationToken.accountId, Config.WEBHOOK_URL)))
                .map(webhookResponse -> webhookResponse.webhook);
    }

    public boolean isStateValid(String state) {
        return randomId.equals(state);
    }
}