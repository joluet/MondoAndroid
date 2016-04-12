package tech.jonas.mondoandroid.api;

import android.app.Application;

import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import tech.jonas.mondoandroid.api.authentication.AccessToken;
import tech.jonas.mondoandroid.api.authentication.OauthInterceptor;
import tech.jonas.mondoandroid.di.scopes.ApiScope;
import tech.jonas.mondoandroid.features.home.HomeStringProvider;

import static com.jakewharton.byteunits.DecimalByteUnit.MEGABYTES;

@Module
public final class ApiModule {
    public static final String PREF_KEY_ACCESS_TOKEN = "access-token";
    private static final int DISK_CACHE_SIZE = (int) MEGABYTES.toBytes(50);
    private static final HttpUrl PRODUCTION_API_URL = HttpUrl.parse(Config.BASE_URL);
    private static final HttpUrl GCM_API_URL = HttpUrl.parse(Config.GCM_URL);

    private OkHttpClient.Builder createApiClient(OkHttpClient client, OauthInterceptor oauthInterceptor) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return client.newBuilder()
                .addInterceptor(oauthInterceptor)
                .addInterceptor(interceptor);
    }

    private OkHttpClient.Builder createOkHttpClient(Application app) {
        // Install an HTTP cache in the application cache directory.
        File cacheDir = new File(app.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);

        return new OkHttpClient.Builder()
                .cache(cache);
    }

    @Provides
    @ApiScope
    OkHttpClient provideOkHttpClient(Application app) {
        return createOkHttpClient(app).build();
    }

    @Provides
    @ApiScope
    Gson provideGson() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    @Provides
    @ApiScope
    @Named("mondo")
    HttpUrl provideBaseUrl() {
        return PRODUCTION_API_URL;
    }

    @Provides
    @ApiScope
    @Named("mondo")
    OkHttpClient provideApiClient(OkHttpClient client,
                                  OauthInterceptor oauthInterceptor) {
        return createApiClient(client, oauthInterceptor).build();
    }

    @Provides
    @ApiScope
    @Named("mondo")
    Retrofit provideMondoRetrofit(@Named("mondo") HttpUrl baseUrl, @Named("mondo") OkHttpClient client, Gson gson) {
        return new Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    @Provides
    @ApiScope
    MondoService provideMondoService(@Named("mondo") Retrofit retrofit) {
        return retrofit.create(MondoService.class);
    }

    @Provides
    @ApiScope
    @Named("gcm")
    HttpUrl provideGcmBaseUrl() {
        return GCM_API_URL;
    }

    @Provides
    @ApiScope
    @Named("gcm")
    OkHttpClient provideGcmClient(OkHttpClient client) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        return client.newBuilder()
                .addInterceptor(interceptor).build();
    }

    @Provides
    @ApiScope
    @Named("gcm")
    Retrofit provideGcmRetrofit(@Named("gcm") HttpUrl baseUrl, @Named("gcm") OkHttpClient client, Gson gson) {
        return new Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    @Provides
    @ApiScope
    GcmService provideGcmService(@Named("gcm") Retrofit retrofit) {
        return retrofit.create(GcmService.class);
    }

    @Provides
    @ApiScope
    @AccessToken
    Preference<String> provideAccessToken(RxSharedPreferences prefs) {
        return prefs.getString(PREF_KEY_ACCESS_TOKEN);
    }

    @Provides
    @ApiScope
    HomeStringProvider provideStringProvider(Application application) {
        return new HomeStringProvider(application);
    }

}