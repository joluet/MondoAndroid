package tech.jonas.mondoandroid.features.transaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import javax.inject.Inject;

import tech.jonas.mondoandroid.R;
import tech.jonas.mondoandroid.api.ApiComponent;
import tech.jonas.mondoandroid.di.ComponentProvider;
import tech.jonas.mondoandroid.ui.model.UiTransaction;
import tech.jonas.mondoandroid.utils.UiUtils;

public class TransactionActivity extends AppCompatActivity implements TransactionView {

    public static final String ARG_TRANSACTION = "arg_transaction";
    @Inject TransactionPresenter presenter;
    private TextView amountView;
    private TextView merchantView;
    private ImageView logoView;

    public static void start(Activity activity, UiTransaction transaction, View... views) {
        final Bundle extras = new Bundle();
        extras.putSerializable(ARG_TRANSACTION, transaction);
        final Intent activityIntent = new Intent(activity, TransactionActivity.class);
        activityIntent.putExtras(extras);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(activity, UiUtils.toPairs(views));
        activity.startActivity(activityIntent, options.toBundle());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        amountView = (TextView) findViewById(R.id.tv_amount);
        merchantView = (TextView) findViewById(R.id.tv_merchant);
        logoView = (ImageView) findViewById(R.id.iv_logo);

        ApiComponent apiComponent = ((ComponentProvider<ApiComponent>) getApplicationContext()).getComponent();
        DaggerTransactionComponent.builder()
                .apiComponent(apiComponent)
                .transactionModule(new TransactionModule(this))
                .build().inject(this);

        final UiTransaction transaction = (UiTransaction) getIntent().getSerializableExtra(ARG_TRANSACTION);
        presenter.onBindView(transaction);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onUnBindView();
    }

    @Override
    public void setAmount(String formattedAmount) {
        amountView.setText(formattedAmount);
    }

    @Override
    public void setMerchantName(String merchantName) {
        merchantView.setText(merchantName);
    }

    @Override
    public void setLogoUrl(String logoUrl) {
        Glide.with(this)
                .load(logoUrl)
                .centerCrop()
                .into(logoView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
