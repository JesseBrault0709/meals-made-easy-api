package app.mealsmadeeasy.api.auth;

import app.mealsmadeeasy.api.security.AuthToken;

public final class LoginDetails {

    private final String username;
    private final AuthToken accessToken;
    private final AuthToken refreshToken;

    public LoginDetails(String username, AuthToken accessToken, AuthToken refreshToken) {
        this.username = username;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getUsername() {
        return this.username;
    }

    public AuthToken getAccessToken() {
        return this.accessToken;
    }

    public AuthToken getRefreshToken() {
        return this.refreshToken;
    }

}
