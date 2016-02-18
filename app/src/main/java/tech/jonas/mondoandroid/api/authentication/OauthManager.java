package tech.jonas.mondoandroid.api.authentication;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.f2prateek.rx.preferences.Preference;
import com.google.gson.Gson;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tech.jonas.mondoandroid.Constants;
import tech.jonas.mondoandroid.api.Config;
import tech.jonas.mondoandroid.data.IntentFactory;

@Singleton
public final class OauthManager {
    private final IntentFactory intentFactory;
    private final OkHttpClient client;
    private final Preference<String> accessToken;
    private final Gson gson;
    private final Application application;

    @Inject
    public OauthManager(IntentFactory intentFactory, OkHttpClient client, Gson gson,
                        @AccessToken Preference<String> accessToken, Application application) {
        this.intentFactory = intentFactory;
        this.client = client;
        this.gson = gson;
        this.accessToken = accessToken;
        this.application = application;
    }

    public Intent createLoginIntent() {
        HttpUrl authorizeUrl = HttpUrl.parse("https://auth.getmondo.co.uk") //
                .newBuilder() //
                .addQueryParameter("client_id", Config.CLIENT_ID) //
                .addQueryParameter("redirect_uri", "mondo://login") //
                .addQueryParameter("response_type", "code") //
                .addQueryParameter("state", "sdnfklj34345klj5") //
                .build();

        return intentFactory.createUrlIntent(authorizeUrl.toString());
    }

    public void handleResult(Uri data) {
        if (data == null) return;

        String code = data.getQueryParameter("code");
        if (code == null) return;

        try {
            // Trade our code for an access token.
            Request request = new Request.Builder() //
                    .url("https://api.getmondo.co.uk/oauth2/token") //
                    .header("Accept", "application/json") //
                    .post(new FormBody.Builder() //
                            .add("grant_type", "authorization_code")
                            .add("client_id", Config.CLIENT_ID) //
                            .add("client_secret", Config.CLIENT_SECRET) //
                            .add("redirect_uri", "mondo://login")
                            .add("code", code) //
                            .build()) //
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                AccessTokenResponse accessTokenResponse =
                        gson.getAdapter(AccessTokenResponse.class).fromJson(response.body().string());
                if (accessTokenResponse != null && accessTokenResponse.access_token != null) {
                    accessToken.set(accessTokenResponse.access_token);

                    Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
                            .putExtra(Constants.EXTENDED_DATA_AUTH_SUCCESS, true);
                    LocalBroadcastManager.getInstance(application).sendBroadcast(localIntent);
                }
            }
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Failed to get access token.");
        }
    }

    private static final class AccessTokenResponse {
        public final String access_token;

        private AccessTokenResponse(String access_token, String scope) {
            this.access_token = access_token;
        }
    }
}