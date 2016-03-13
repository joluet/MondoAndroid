package tech.jonas.mondoandroid.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.f2prateek.rx.preferences.Preference;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.Collections;

import javax.inject.Inject;

import dagger.ObjectGraph;
import retrofit2.adapter.rxjava.HttpException;
import tech.jonas.mondoandroid.Constants;
import tech.jonas.mondoandroid.R;
import tech.jonas.mondoandroid.api.ApiModule;
import tech.jonas.mondoandroid.api.Config;
import tech.jonas.mondoandroid.api.MondoService;
import tech.jonas.mondoandroid.api.authentication.AccessToken;
import tech.jonas.mondoandroid.api.authentication.OauthManager;
import tech.jonas.mondoandroid.api.authentication.OauthService;
import tech.jonas.mondoandroid.data.Injector;
import tech.jonas.mondoandroid.ui.model.BalanceMapper;
import tech.jonas.mondoandroid.ui.model.TransactionMapper;
import tech.jonas.mondoandroid.utils.RxUtils;

public class MainActivity extends RxAppCompatActivity {

    @Inject OauthManager oauthManager;
    @Inject MondoService mondoService;
    @Inject @AccessToken Preference<String> accessToken;
    private Toolbar toolbar;
    private RecyclerView rvTransactions;
    private TransactionAdapter transactionAdapter;
    private ObjectGraph activityGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Explicitly reference the application object since we don't want to match our own injector.
        ObjectGraph appGraph = Injector.obtain(getApplication());
        appGraph.inject(this);
        activityGraph = appGraph.plus(new ApiModule());

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Setup recyclerview
        rvTransactions = (RecyclerView) findViewById(R.id.rv_transactions);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvTransactions.setLayoutManager(layoutManager);
        transactionAdapter = new TransactionAdapter(getApplicationContext());
        rvTransactions.setAdapter(transactionAdapter);
        transactionAdapter.setOnTransactionClickListener(v ->
                Snackbar.make(rvTransactions, "show details", Snackbar.LENGTH_SHORT).show());

        BroadcastReceiver authResultReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getTransactionsAndUpdateUI();
            }
        };

        // The filter's action is BROADCAST_ACTION
        IntentFilter intentFilter = new IntentFilter(
                Constants.BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                authResultReceiver, intentFilter);

        if (!accessToken.isSet() && getIntent().getData() == null) {
            startLoginActivity();
        } else if (getIntent().getData() != null && "mondo.co.uk".equals(getIntent().getData().getHost())) {
            Intent serviceIntent = new Intent(this, OauthService.class);
            serviceIntent.setData(getIntent().getData());
            startService(serviceIntent);
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


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Uri data = intent.getData();
        if (data == null) return;

        if ("mondo".equals(data.getScheme())) {
            Intent serviceIntent = new Intent(this, OauthService.class);
            serviceIntent.setData(data);
            startService(serviceIntent);
        }
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
                                startLoginActivity();
                            }
                        });
    }

    private void startLoginActivity() {
        final Intent loginIntent = oauthManager.createLoginIntent();
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(loginIntent);

    }

    @Override
    public Object getSystemService(@NonNull String name) {
        if (Injector.matchesService(name)) {
            return activityGraph;
        }
        return super.getSystemService(name);
    }
}
