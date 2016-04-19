package tech.jonas.mondoandroid.features.transaction;

public interface TransactionView {
    void setAmount(String formattedAmount);

    void setMerchantName(String merchantName);

    void setAverageSpend(String averageSpend);

    void setLogoUrl(String logoUrl);
}
