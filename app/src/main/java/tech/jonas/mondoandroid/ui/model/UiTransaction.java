package tech.jonas.mondoandroid.ui.model;

public class UiTransaction {

    public final String formattedAmount;
    public final String description;
    public final String category;
    public final String created;
    public final DeclineReason declineReason;
    public final String merchantName;
    public final String merchantLogo;

    public UiTransaction(String formattedAmount, String description, String category, String created, DeclineReason declineReason, String merchantName, String merchantLogo) {
        this.formattedAmount = formattedAmount;
        this.description = description;
        this.category = category;
        this.created = created;
        this.declineReason = declineReason;
        this.merchantName = merchantName;
        this.merchantLogo = merchantLogo;
    }

    public boolean isDeclined() {
        return declineReason != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UiTransaction that = (UiTransaction) o;

        if (formattedAmount != null ? !formattedAmount.equals(that.formattedAmount) : that.formattedAmount != null)
            return false;
        if (description != null ? !description.equals(that.description) : that.description != null)
            return false;
        if (category != null ? !category.equals(that.category) : that.category != null)
            return false;
        if (created != null ? !created.equals(that.created) : that.created != null) return false;
        if (declineReason != that.declineReason) return false;
        if (merchantName != null ? !merchantName.equals(that.merchantName) : that.merchantName != null)
            return false;
        return merchantLogo != null ? merchantLogo.equals(that.merchantLogo) : that.merchantLogo == null;
    }

    @Override
    public String toString() {
        return "UiTransaction{" +
                "formattedAmount='" + formattedAmount + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", created='" + created + '\'' +
                ", declineReason=" + declineReason +
                ", merchantName='" + merchantName + '\'' +
                ", merchantLogo='" + merchantLogo + '\'' +
                '}';
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
}
