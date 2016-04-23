package tech.jonas.mondoandroid.features.transaction;

public interface TransactionView {
    void setWindowTitle(String title);

    void addMapMarker(double lat, double lng, String title);

    void moveMapTo(double lat, double lng);

    void setAmount(String formattedAmount);

    void setAverageSpend(String averageSpend);

    void setLogoUrl(String logoUrl);
}
