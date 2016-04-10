package tech.jonas.mondoandroid.features.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.List;

import javax.inject.Inject;

import tech.jonas.mondoandroid.R;
import tech.jonas.mondoandroid.api.ApiComponent;
import tech.jonas.mondoandroid.api.authentication.OauthManager;
import tech.jonas.mondoandroid.di.ComponentProvider;
import tech.jonas.mondoandroid.ui.model.Transaction;

public class MainActivity extends RxAppCompatActivity implements HomeView {

    @Inject HomePresenter presenter;
    @Inject OauthManager oauthManager;
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

        presenter.onBindView(getIntent().getData());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onUnBindView();
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
    public void setTitle(String title) {
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
    }

    @Override
    public void startLoginActivity() {
        final Intent loginIntent = oauthManager.createLoginIntent();
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(loginIntent);

    }

    @Override
    public void setTransactions(List<Transaction> transactions) {
        transactionAdapter.setTransactions(transactions);
    }

}
