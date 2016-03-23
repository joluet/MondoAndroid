package tech.jonas.mondoandroid.api;

import com.google.gson.Gson;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import tech.jonas.mondoandroid.api.authentication.OauthInterceptor;
import tech.jonas.mondoandroid.gcm.GcmListenerService;
import tech.jonas.mondoandroid.ui.MainActivity;

@Module(
        complete = false,
        library = true,
        injects = {
                MainActivity.class,
                GcmListenerService.class
        }
)
public final class ApiModule {
    public static final HttpUrl PRODUCTION_API_URL = HttpUrl.parse(Config.BASE_URL);
    private static final HttpUrl GCM_API_URL = HttpUrl.parse(Config.GCM_URL);

    static OkHttpClient.Builder createApiClient(OkHttpClient client, OauthInterceptor oauthInterceptor) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return client.newBuilder()
                .addInterceptor(oauthInterceptor)
                .addInterceptor(interceptor);
    }

    @Provides
    @Singleton
    @Named("mondo")
    HttpUrl provideBaseUrl() {
        return PRODUCTION_API_URL;
    }

    @Provides
    @Singleton
    @Named("mondo")
    OkHttpClient provideApiClient(OkHttpClient client,
                                  OauthInterceptor oauthInterceptor) {
        return createApiClient(client, oauthInterceptor).build();
    }

    @Provides
    @Singleton
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
    @Singleton
    MondoService provideMondoService(@Named("mondo") Retrofit retrofit) {
        return retrofit.create(MondoService.class);
    }

    @Provides
    @Singleton
    @Named("gcm")
    HttpUrl provideGcmBaseUrl() {
        return GCM_API_URL;
    }

    @Provides
    @Singleton
    @Named("gcm")
    OkHttpClient provideGcmClient(OkHttpClient client) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        return client.newBuilder()
                .addInterceptor(interceptor).build();
    }

    @Provides
    @Singleton
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
    @Singleton
    GcmService provideGcmService(@Named("gcm") Retrofit retrofit) {
        return retrofit.create(GcmService.class);
    }
}