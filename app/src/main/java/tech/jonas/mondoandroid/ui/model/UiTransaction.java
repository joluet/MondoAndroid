package tech.jonas.mondoandroid.ui.model;

import android.support.annotation.Nullable;

import java.io.Serializable;

public class UiTransaction implements Serializable {

    public final String id;
    public final String formattedAmount;
    public final String description;
    public final String category;
    public final String created;
    public final DeclineReason declineReason;
    @Nullable public final String merchantName;
    @Nullable public final String merchantLogo;
    @Nullable public final Spending spending;
    public final double latitude;
    public final double longitude;

    public UiTransaction(String id, String formattedAmount, String description, String category, String created, DeclineReason declineReason, String merchantName, String merchantLogo, Spending spending, double latitude, double longitude) {
        this.id = id;
        this.formattedAmount = formattedAmount;
        this.description = description;
        this.category = category;
        this.created = created;
        this.declineReason = declineReason;
        this.merchantName = merchantName;
        this.merchantLogo = merchantLogo;
        this.spending = spending;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private UiTransaction(Builder builder) {
        id = builder.id;
        formattedAmount = builder.formattedAmount;
        description = builder.description;
        category = builder.category;
        created = builder.created;
        declineReason = builder.declineReason;
        merchantName = builder.merchantName;
        merchantLogo = builder.merchantLogo;
        spending = builder.spending;
        latitude = builder.latitude;
        longitude = builder.longitude;
    }

    public static IId builder() {
        return new Builder();
    }

    public boolean isDeclined() {
        return declineReason != null;
    }

    public boolean hasSpending() {
        return spending != null;
    }

    public boolean hasMerchant() {
        return merchantName != null;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UiTransaction that = (UiTransaction) o;

        if (Double.compare(that.latitude, latitude) != 0) return false;
        if (Double.compare(that.longitude, longitude) != 0) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
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
        if (merchantLogo != null ? !merchantLogo.equals(that.merchantLogo) : that.merchantLogo != null)
            return false;
        if (spending != null ? !spending.equals(that.spending) : that.spending != null)
            return false;

        return true;
    }

    @Override
    public String toString() {
        return "UiTransaction{" +
                "id='" + id + '\'' +
                ", formattedAmount='" + formattedAmount + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", created='" + created + '\'' +
                ", declineReason=" + declineReason +
                ", merchantName='" + merchantName + '\'' +
                ", merchantLogo='" + merchantLogo + '\'' +
                ", spending=" + spending +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }


    interface IBuild {
        UiTransaction build();
    }

    interface ILongitude {
        IBuild withLongitude(double val);
    }

    interface ILatitude {
        ILongitude withLatitude(double val);
    }

    interface ISpending {
        ILatitude withSpending(Spending val);
        UiTransaction build();
    }

    interface IMerchantLogo {
        ISpending withMerchantLogo(String val);
    }

    interface IMerchantName {
        IMerchantLogo withMerchantName(String val);
        UiTransaction build();
    }

    interface IDeclineReason {
        IMerchantName withDeclineReason(DeclineReason val);
    }

    interface ICreated {
        IDeclineReason withCreated(String val);
    }

    interface ICategory {
        ICreated withCategory(String val);
    }

    interface IDescription {
        ICategory withDescription(String val);
    }

    interface IFormattedAmount {
        IDescription withFormattedAmount(String val);
    }

    interface IId {
        IFormattedAmount withId(String val);
    }

    public static final class Builder implements ILongitude, ILatitude, ISpending, IMerchantLogo, IMerchantName, IDeclineReason, ICreated, ICategory, IDescription, IFormattedAmount, IId, IBuild {
        private double longitude;
        private double latitude;
        private Spending spending;
        private String merchantLogo;
        private String merchantName;
        private DeclineReason declineReason;
        private String created;
        private String category;
        private String description;
        private String formattedAmount;
        private String id;

        private Builder() {
        }

        @Override
        public IBuild withLongitude(double val) {
            longitude = val;
            return this;
        }

        @Override
        public ILongitude withLatitude(double val) {
            latitude = val;
            return this;
        }

        @Override
        public ILatitude withSpending(Spending val) {
            spending = val;
            return this;
        }

        @Override
        public ISpending withMerchantLogo(String val) {
            merchantLogo = val;
            return this;
        }

        @Override
        public IMerchantLogo withMerchantName(String val) {
            merchantName = val;
            return this;
        }

        @Override
        public IMerchantName withDeclineReason(DeclineReason val) {
            declineReason = val;
            return this;
        }

        @Override
        public IDeclineReason withCreated(String val) {
            created = val;
            return this;
        }

        @Override
        public ICreated withCategory(String val) {
            category = val;
            return this;
        }

        @Override
        public ICategory withDescription(String val) {
            description = val;
            return this;
        }

        @Override
        public IDescription withFormattedAmount(String val) {
            formattedAmount = val;
            return this;
        }

        @Override
        public IFormattedAmount withId(String val) {
            id = val;
            return this;
        }

        public UiTransaction build() {
            return new UiTransaction(this);
        }
    }
}
