package tech.jonas.mondoandroid.features.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import tech.jonas.mondoandroid.R;
import tech.jonas.mondoandroid.api.authentication.OauthManager;

public class LoginActivity extends RxAppCompatActivity {


    public static void start(Activity activity) {
        final Bundle extras = new Bundle();
        final Intent activityIntent = new Intent(activity, LoginActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activityIntent.putExtras(extras);
        activity.startActivity(activityIntent);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle(getString(R.string.welcome));

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.pb_loading_spinner);

        final WebView webView = (WebView) findViewById(R.id.webview_login);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView webView, String url) {
                progressBar.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }
        });

        webView.loadUrl(OauthManager.getAuthUrl());
    }
}
