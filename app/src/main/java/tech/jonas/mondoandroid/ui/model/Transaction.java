package tech.jonas.mondoandroid.ui.model;

public class Transaction {
    public final String formattedAmount;
    public final String description;
    public final String category;
    public final String settled;
    public final String merchantName;
    public final String merchantLogo;

    public Transaction(String formattedAmount, String description, String category, String settled, String merchantName, String merchantLogo) {
        this.formattedAmount = formattedAmount;
        this.description = description;
        this.category = category;
        this.settled = settled;
        this.merchantName = merchantName;
        this.merchantLogo = merchantLogo;
    }
}
