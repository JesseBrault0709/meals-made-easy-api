package app.mealsmadeeasy.api.auth;

public final class LoginView {

    private final String username;
    private final String accessToken;

    public LoginView(String username, String accessToken) {
        this.username = username;
        this.accessToken = accessToken;
    }

    public String getUsername() {
        return this.username;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

}
