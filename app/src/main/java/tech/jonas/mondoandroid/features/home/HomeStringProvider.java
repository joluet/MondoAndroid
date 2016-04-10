package tech.jonas.mondoandroid.features.home;

import android.content.Context;

import tech.jonas.mondoandroid.R;

public class HomeStringProvider {

    private final Context context;

    public HomeStringProvider(Context context) {
        this.context = context;
    }

    public String getFormattedGbp(double amount, String emoji) {
        return context.getString(R.string.formatted_amount_gbp, amount, emoji);
    }

    public String getFormattedBalance(String formattedAmount) {
        return context.getString(R.string.formatted_balance, formattedAmount);
    }
}
