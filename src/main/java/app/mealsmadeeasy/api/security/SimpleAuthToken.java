package app.mealsmadeeasy.api.security;

import java.time.LocalDateTime;

public final class SimpleAuthToken implements AuthToken {

    private final String token;
    private final long lifetime;
    private final LocalDateTime expires;

    public SimpleAuthToken(String token, long lifetime, LocalDateTime expires) {
        this.token = token;
        this.lifetime = lifetime;
        this.expires = expires;
    }

    @Override
    public String getToken() {
        return this.token;
    }

    @Override
    public long getLifetime() {
        return this.lifetime;
    }

    @Override
    public LocalDateTime getExpires() {
        return this.expires;
    }

}
