package tech.jonas.mondoandroid.ui.model;

public class Transaction {

    public boolean isDeclined() {
        return declineReason != null;
    }

    public enum DeclineReason {
        INSUFFICIENT_FUNDS,
        CARD_INACTIVE,
        CARD_BLOCKED,
        OTHER;

        public static DeclineReason parse(String declineReason) {
            switch (declineReason) {
                case "INSUFFICIENT_FUNDS":
                    return INSUFFICIENT_FUNDS;
                case "CARD_INACTIVE":
                    return CARD_INACTIVE;
                case "CARD_BLOCKED":
                    return CARD_BLOCKED;
                case "OTHER":
                    return OTHER;
                default:
                    throw new UnsupportedOperationException("Unknown decline reason: " + declineReason);
            }
        }
    }

    public final String formattedAmount;
    public final String description;
    public final String category;
    public final String settled;
    public final DeclineReason declineReason;
    public final String merchantName;
    public final String merchantLogo;

    public Transaction(String formattedAmount, String description, String category, String settled, DeclineReason declineReason, String merchantName, String merchantLogo) {
        this.formattedAmount = formattedAmount;
        this.description = description;
        this.category = category;
        this.settled = settled;
        this.declineReason = declineReason;
        this.merchantName = merchantName;
        this.merchantLogo = merchantLogo;
    }
}
