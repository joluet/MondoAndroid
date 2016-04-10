package tech.jonas.mondoandroid.ui.model;

import rx.Observable;
import tech.jonas.mondoandroid.features.home.HomeStringProvider;

public class BalanceMapper {

    public static final double CENTS_PER_POUND = 100d;

    public static Observable.Transformer<tech.jonas.mondoandroid.api.model.Balance, Balance> map(HomeStringProvider stringProvider) {
        return observable -> observable.map(balance -> map(stringProvider, balance));
    }

    private static Balance map(HomeStringProvider stringProvider, tech.jonas.mondoandroid.api.model.Balance apiBalance) {
        final double balance = apiBalance.balance / CENTS_PER_POUND;
        return new Balance(stringProvider.getFormattedGbp(balance, ""));
    }
}
