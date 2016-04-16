package tech.jonas.mondoandroid.api.model;

public class AccessTokenResponse {
    public final String accessToken;
    public final String refreshToken;

    public AccessTokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
