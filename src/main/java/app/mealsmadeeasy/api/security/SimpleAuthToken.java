package app.mealsmadeeasy.api.security;

public final class SimpleAuthToken implements AuthToken {

    private final String token;
    private final long lifetime;

    public SimpleAuthToken(String token, long lifetime) {
        this.token = token;
        this.lifetime = lifetime;
    }

    @Override
    public String getToken() {
        return this.token;
    }

    @Override
    public long getLifetime() {
        return this.lifetime;
    }

}
