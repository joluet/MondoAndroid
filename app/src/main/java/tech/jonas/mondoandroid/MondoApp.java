package tech.jonas.mondoandroid;

import android.app.Application;

import tech.jonas.mondoandroid.api.ApiComponent;
import tech.jonas.mondoandroid.api.ApiModule;
import tech.jonas.mondoandroid.api.DaggerApiComponent;
import tech.jonas.mondoandroid.di.ComponentProvider;
import tech.jonas.mondoandroid.di.DaggerMondoComponent;
import tech.jonas.mondoandroid.di.MondoComponent;
import tech.jonas.mondoandroid.di.MondoModule;

public final class MondoApp extends Application implements ComponentProvider<ApiComponent> {
    private ApiComponent apiComponent;

    @Override
    public void onCreate() {
        super.onCreate();


        final MondoComponent mondoComponent = DaggerMondoComponent.builder()
                .mondoModule(new MondoModule(this))
                .build();

        apiComponent = DaggerApiComponent.builder()
                .mondoComponent(mondoComponent)
                .apiModule(new ApiModule())
                .build();
    }


    @Override
    public ApiComponent getComponent() {
        return apiComponent;
    }
}