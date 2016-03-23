package tech.jonas.mondoandroid.api.model;

public class RegistrationToken {

    public final String accountId;
    public final String token;

    public RegistrationToken(String accountId, String token) {
        this.accountId = accountId;
        this.token = token;
    }
}
