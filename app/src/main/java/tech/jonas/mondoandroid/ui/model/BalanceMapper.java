package tech.jonas.mondoandroid.ui.model;

import android.content.Context;

import rx.Observable;
import tech.jonas.mondoandroid.R;

public class BalanceMapper {

    public static final double CENTS_PER_POUND = 100d;

    public static Observable.Transformer<tech.jonas.mondoandroid.api.model.Balance, Balance> map(Context context) {
        return observable -> observable.map(balance -> map(context, balance));
    }

    private static Balance map(Context context, tech.jonas.mondoandroid.api.model.Balance apiBalance) {
        final double balance = apiBalance.balance / CENTS_PER_POUND;
        return new Balance(context.getString(R.string.formatted_amount_gbp, balance, ""));
    }
}
