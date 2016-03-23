package tech.jonas.mondoandroid.api.model;

public class AccessTokenResponse {
    public final String accessToken;

    private AccessTokenResponse(String accessToken, String scope) {
        this.accessToken = accessToken;
    }
}
