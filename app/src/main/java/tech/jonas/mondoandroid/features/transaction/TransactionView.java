package tech.jonas.mondoandroid.features.transaction;

public interface TransactionView {
    void setAmount(String formattedAmount);

    void setMerchantName(String merchantName);

    void setLogoUrl(String logoUrl);
}
