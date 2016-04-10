package tech.jonas.mondoandroid.features.home;

import android.net.Uri;

public interface HomePresenter {

    void onBindView(Uri uri);

    void onUnBindView();

}
