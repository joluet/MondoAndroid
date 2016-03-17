package tech.jonas.mondoandroid.api.model;

public class Transaction {
    public final long amount;
    public final String description;
    public final String declineReason;
    public final String currency;
    public final String category;
    public final String created;
    public final long accountBalance;
    public final Merchant merchant;


    public Transaction(long amount, String description, String decline_reason, String currency, String category, String created, long accountBalance, Merchant merchant) {
        this.amount = amount;
        this.description = description;
        this.declineReason = decline_reason;
        this.currency = currency;
        this.category = category;
        this.created = created;
        this.accountBalance = accountBalance;
        this.merchant = merchant;
    }
}
