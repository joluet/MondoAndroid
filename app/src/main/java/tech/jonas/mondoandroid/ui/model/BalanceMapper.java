package tech.jonas.mondoandroid.ui.model;

import rx.Observable;
import tech.jonas.mondoandroid.api.model.Balance;
import tech.jonas.mondoandroid.features.home.HomeStringProvider;

public class BalanceMapper {

    public static final double CENTS_PER_POUND = 100d;

    public static Observable.Transformer<tech.jonas.mondoandroid.api.model.Balance, UiBalance> map(HomeStringProvider stringProvider) {
        return observable -> observable.map(balance -> map(stringProvider, balance));
    }

    private static UiBalance map(HomeStringProvider stringProvider, Balance apiBalance) {
        final double balance = apiBalance.balance / CENTS_PER_POUND;
        return new UiBalance(stringProvider.getFormattedGbp(balance, ""));
    }
}
