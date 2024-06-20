package app.mealsmadeeasy.api.security;

public final class AuthToken {

    private final String token;
    private final long lifetime;

    public AuthToken(String token, long lifetime) {
        this.token = token;
        this.lifetime = lifetime;
    }

    public String getToken() {
        return this.token;
    }

    public long getLifetime() {
        return this.lifetime;
    }

}
