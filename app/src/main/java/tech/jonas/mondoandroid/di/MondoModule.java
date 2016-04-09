
package tech.jonas.mondoandroid.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import tech.jonas.mondoandroid.MondoApp;
import tech.jonas.mondoandroid.data.DataModule;

@Module(
        includes = {
                DataModule.class
        },
        injects = {
                MondoApp.class
        }
)
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
}