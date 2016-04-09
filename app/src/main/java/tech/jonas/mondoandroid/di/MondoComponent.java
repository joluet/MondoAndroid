package tech.jonas.mondoandroid.di;

import android.app.Application;

import com.f2prateek.rx.preferences.RxSharedPreferences;

import javax.inject.Singleton;

import dagger.Component;
import tech.jonas.mondoandroid.data.IntentFactory;

@Singleton
@Component(modules = {MondoModule.class})
public interface MondoComponent {

    // Expose to subgraphs
    Application application();

    RxSharedPreferences rxSharedPreferences();

    IntentFactory intentFactory();
}