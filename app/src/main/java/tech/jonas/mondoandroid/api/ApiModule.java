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
import tech.jonas.mondoandroid.api.authentication.OauthService;
import tech.jonas.mondoandroid.ui.MainActivity;

@Module(
        complete = false,
        library = true,
        injects = {
                OauthService.class,
                MainActivity.class
        }
)
public final class ApiModule {
    public static final HttpUrl PRODUCTION_API_URL = HttpUrl.parse(Config.BASE_URL);

    @Provides
    @Singleton
    HttpUrl provideBaseUrl() {
        return PRODUCTION_API_URL;
    }

    @Provides
    @Singleton
    @Named("Api")
    OkHttpClient provideApiClient(OkHttpClient client,
                                  OauthInterceptor oauthInterceptor) {
        return createApiClient(client, oauthInterceptor).build();
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(HttpUrl baseUrl, @Named("Api") OkHttpClient client, Gson gson) {
        return new Retrofit.Builder() //
                .client(client) //
                .baseUrl(baseUrl) //
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) //
                .build();
    }

    @Provides
    @Singleton
    MondoService provideMondoService(Retrofit retrofit) {
        return retrofit.create(MondoService.class);
    }

    static OkHttpClient.Builder createApiClient(OkHttpClient client, OauthInterceptor oauthInterceptor) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return client.newBuilder()
                .addInterceptor(oauthInterceptor)
                .addInterceptor(interceptor);
    }
}