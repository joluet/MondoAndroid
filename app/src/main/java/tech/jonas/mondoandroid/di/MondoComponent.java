package tech.jonas.mondoandroid.di;

import android.app.Application;

import com.f2prateek.rx.preferences.RxSharedPreferences;

import javax.inject.Singleton;

import dagger.Component;
import tech.jonas.mondoandroid.data.IntentFactory;
import tech.jonas.mondoandroid.features.home.SubscriptionManager;
import tech.jonas.mondoandroid.utils.SchedulerProvider;

@Singleton
@Component(modules = {MondoModule.class})
public interface MondoComponent {

    // Expose to subgraphs
    Application application();

    RxSharedPreferences rxSharedPreferences();

    IntentFactory intentFactory();

    SchedulerProvider schedulerProvider();

    SubscriptionManager subscriptionManager();
}