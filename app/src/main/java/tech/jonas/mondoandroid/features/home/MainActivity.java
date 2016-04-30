package tech.jonas.mondoandroid.features.home;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;
import tech.jonas.mondoandroid.R;
import tech.jonas.mondoandroid.api.ApiComponent;
import tech.jonas.mondoandroid.api.authentication.OauthManager;
import tech.jonas.mondoandroid.di.ComponentProvider;
import tech.jonas.mondoandroid.features.login.LoginActivity;
import tech.jonas.mondoandroid.features.transaction.TransactionActivity;
import tech.jonas.mondoandroid.ui.model.UiTransaction;

public class MainActivity extends RxAppCompatActivity implements HomeView {

    @Inject
    HomePresenter presenter;
    @Inject
    OauthManager oauthManager;
    private Toolbar toolbar;
    private RecyclerView rvTransactions;
    private TransactionAdapter transactionAdapter;
    private SwipeRefreshLayout swipeContainer;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
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
        rvTransactions.setItemAnimator(new FadeInUpAnimator());
        transactionAdapter = new TransactionAdapter(getApplicationContext());
        rvTransactions.setAdapter(transactionAdapter);
        rvTransactions.addItemDecoration(new StickyHeaderDecoration(transactionAdapter));
        transactionAdapter.setOnTransactionClickListener((transaction, views) -> {
            // Add navigation bar view to transition animation
            View navigationBar = findViewById(android.R.id.navigationBarBackground);
            navigationBar.setTransitionName(Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME);
            View[] viewArray = Arrays.copyOf(views, views.length + 1);
            viewArray[viewArray.length - 1] = navigationBar;

            TransactionActivity.start(this, transaction, viewArray);
        });
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);

        // Setup refresh listener which triggers new data loading
        assert swipeContainer != null;
        swipeContainer.setOnRefreshListener(() -> presenter.onRefresh());
        presenter.onBindView(getIntent().getData());
    }

    @Override
    public void setIsLoading(boolean isLoading) {
        swipeContainer.post(() -> swipeContainer.setRefreshing(isLoading));
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
        LoginActivity.start(this);
    }

    @Override
    public void setTransactions(List<UiTransaction> transactions) {
        transactionAdapter.setTransactions(transactions);
    }

}
