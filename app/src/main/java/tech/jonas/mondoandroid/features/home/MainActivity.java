package tech.jonas.mondoandroid.features.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.f2prateek.rx.preferences.Preference;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.Collections;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import tech.jonas.mondoandroid.Constants;
import tech.jonas.mondoandroid.R;
import tech.jonas.mondoandroid.api.ApiComponent;
import tech.jonas.mondoandroid.api.Config;
import tech.jonas.mondoandroid.api.GcmService;
import tech.jonas.mondoandroid.api.MondoService;
import tech.jonas.mondoandroid.api.authentication.AccessToken;
import tech.jonas.mondoandroid.api.authentication.OauthManager;
import tech.jonas.mondoandroid.di.ComponentProvider;
import tech.jonas.mondoandroid.ui.model.BalanceMapper;
import tech.jonas.mondoandroid.ui.model.TransactionMapper;
import tech.jonas.mondoandroid.utils.RxUtils;

public class MainActivity extends RxAppCompatActivity implements HomeView {

    @Inject OauthManager oauthManager;
    @Inject MondoService mondoService;
    @Inject GcmService gcmService;
    @Inject @AccessToken Preference<String> accessToken;
    private Toolbar toolbar;
    private RecyclerView rvTransactions;
    private TransactionAdapter transactionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ApiComponent apiComponent = ((ComponentProvider<ApiComponent>) getApplicationContext()).getComponent();
        DaggerHomeComponent.builder()
                .apiComponent(apiComponent)
                .homeModule(new HomeModule(this))
                .build().inject(this);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Setup recyclerview
        rvTransactions = (RecyclerView) findViewById(R.id.rv_transactions);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvTransactions.setLayoutManager(layoutManager);
        transactionAdapter = new TransactionAdapter(getApplicationContext());
        rvTransactions.setAdapter(transactionAdapter);
        rvTransactions.addItemDecoration(new StickyRecyclerHeadersDecoration(transactionAdapter));
        transactionAdapter.setOnTransactionClickListener(v ->
                Snackbar.make(rvTransactions, "show details", Snackbar.LENGTH_SHORT).show());

        BroadcastReceiver authResultReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getTransactionsAndUpdateUI();
            }
        };

        // The filter's action is BROADCAST_ACTION
        IntentFilter intentFilter = new IntentFilter(Constants.BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(authResultReceiver, intentFilter);
        final Uri intentData = getIntent().getData();
        if (!accessToken.isSet() && intentData == null) {
            startLoginActivity();
        } else if (intentData != null && "mondo.co.uk".equals(intentData.getHost())) {
            final Uri data = getIntent().getData();
            final Observable<String> tokenObservable = oauthManager.getAuthToken(data).cache();

            // Obtain auth token
            tokenObservable.compose(RxUtils.applySchedulers())
                    .subscribe(token -> {
                        getTransactionsAndUpdateUI();
                    });

            // Register webhook for notifications
            tokenObservable.flatMap(token -> oauthManager.registerWebhook())
                    .subscribe(webhook -> Log.d(getClass().getSimpleName(), "Webhook id: " + webhook.id));
        }
        if (accessToken.isSet()) {
            getTransactionsAndUpdateUI();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getTransactionsAndUpdateUI() {
        mondoService.getBalance(Config.ACCOUNT_ID)
                .compose(bindToLifecycle())
                .compose(RxUtils.applySchedulers())
                .compose(BalanceMapper.map(this))
                .subscribe(balance -> {
                    toolbar.setTitle(getString(R.string.formatted_balance, balance.formattedAmount));
                    setSupportActionBar(toolbar);
                }, throwable -> {
                    if (throwable instanceof HttpException) {
                        startLoginActivity();
                    }
                });
        mondoService.getTransactions(Config.ACCOUNT_ID, "merchant")
                .compose(bindToLifecycle())
                .compose(RxUtils.applySchedulers())
                .compose(TransactionMapper.map(this))
                .map(transactions -> {
                    Collections.reverse(transactions);
                    return transactions;
                })
                .subscribe(transactionAdapter::setTransactions,
                        throwable -> {
                            if (throwable instanceof HttpException) {
                                accessToken.delete();
                                startLoginActivity();
                            }
                        });
    }

    private void startLoginActivity() {
        final Intent loginIntent = oauthManager.createLoginIntent();
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(loginIntent);

    }

}
