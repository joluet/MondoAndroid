package tech.jonas.mondoandroid.api.authentication;

import android.app.IntentService;
import android.content.Intent;

import javax.inject.Inject;

import dagger.ObjectGraph;
import tech.jonas.mondoandroid.data.Injector;

public final class OauthService extends IntentService {
    @Inject
    OauthManager oauthManager;

    public OauthService() {
        super(OauthService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ObjectGraph appGraph = Injector.obtain(getApplication());
        appGraph.inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        oauthManager.handleResult(intent.getData());
    }
}