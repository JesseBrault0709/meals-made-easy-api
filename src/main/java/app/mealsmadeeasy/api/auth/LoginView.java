package app.mealsmadeeasy.api.auth;

import java.time.LocalDateTime;

public final class LoginView {

    private final String username;
    private final String accessToken;
    private final LocalDateTime expires;

    public LoginView(String username, String accessToken, LocalDateTime expires) {
        this.username = username;
        this.accessToken = accessToken;
        this.expires = expires;
    }

    public String getUsername() {
        return this.username;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public LocalDateTime getExpires() {
        return this.expires;
    }

}
