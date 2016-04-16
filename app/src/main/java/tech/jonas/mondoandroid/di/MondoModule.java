
package tech.jonas.mondoandroid.di;

import android.app.Application;
import android.content.SharedPreferences;

import com.f2prateek.rx.preferences.RxSharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tech.jonas.mondoandroid.MondoApp;
import tech.jonas.mondoandroid.data.IntentFactory;
import tech.jonas.mondoandroid.features.home.SubscriptionManager;
import tech.jonas.mondoandroid.utils.SchedulerProvider;

import static android.content.Context.MODE_PRIVATE;

@Module
public final class MondoModule {
    private final MondoApp app;

    public MondoModule(MondoApp app) {
        this.app = app;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return app;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application app) {
        return app.getSharedPreferences("mondo", MODE_PRIVATE);
    }

    @Provides
    @Singleton
    RxSharedPreferences provideRxSharedPreferences(SharedPreferences prefs) {
        return RxSharedPreferences.create(prefs);
    }

    @Provides
    @Singleton
    IntentFactory provideIntentFactory() {
        return IntentFactory.REAL;
    }

    @Provides
    @Singleton
    SchedulerProvider provideSchedulerProvider() {
        return new SchedulerProvider(AndroidSchedulers.mainThread(), Schedulers.io());
    }

    @Provides
    SubscriptionManager provideSubscriptionManager() {
        return new SubscriptionManager();
    }
}