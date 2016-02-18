package tech.jonas.mondoandroid.api.model;

public class Balance {
    public final long balance;
    public final String currency;
    public final long spendToday;

    public Balance(long balance, String currency, long spendToday) {
        this.balance = balance;
        this.currency = currency;
        this.spendToday = spendToday;
    }
}
