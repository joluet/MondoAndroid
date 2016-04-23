package tech.jonas.mondoandroid.api.model;

public class Merchant {

    public final String id;
    public final String name;
    public final String emoji;
    public final String logo;
    public final Address address;


    public Merchant(String id, String name, String emoji, String logo, Address address) {
        this.id = id;
        this.name = name;
        this.emoji = emoji;
        this.logo = logo;
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Merchant merchant = (Merchant) o;

        return id != null ? id.equals(merchant.id) : merchant.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
