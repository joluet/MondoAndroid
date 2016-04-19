package tech.jonas.mondoandroid.api.model;

public class Merchant {

    public final String id;
    public final String name;
    public final String emoji;
    public final String logo;

    public Merchant(String id, String name, String emoji, String logo) {
        this.id = id;
        this.name = name;
        this.emoji = emoji;
        this.logo = logo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Merchant merchant = (Merchant) o;

        if (id != null ? !id.equals(merchant.id) : merchant.id != null) return false;
        if (name != null ? !name.equals(merchant.name) : merchant.name != null) return false;
        if (emoji != null ? !emoji.equals(merchant.emoji) : merchant.emoji != null) return false;
        if (logo != null ? !logo.equals(merchant.logo) : merchant.logo != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (emoji != null ? emoji.hashCode() : 0);
        result = 31 * result + (logo != null ? logo.hashCode() : 0);
        return result;
    }
}
