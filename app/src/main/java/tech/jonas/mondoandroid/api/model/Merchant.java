package tech.jonas.mondoandroid.api.model;

public class Merchant {

    public final String name;
    public final String emoji;
    public final String logo;

    public Merchant(String name, String emoji, String logo) {
        this.name = name;
        this.emoji = emoji;
        this.logo = logo;
    }
}
