package tech.jonas.mondoandroid;

import android.app.Application;
import android.support.annotation.NonNull;

import dagger.ObjectGraph;
import tech.jonas.mondoandroid.data.Injector;

public final class MondoApp extends Application {
    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        objectGraph = ObjectGraph.create(Modules.list(this));
        objectGraph.inject(this);

    }

    @Override
    public Object getSystemService(@NonNull String name) {
        if (Injector.matchesService(name)) {
            return objectGraph;
        }
        return super.getSystemService(name);
    }
}