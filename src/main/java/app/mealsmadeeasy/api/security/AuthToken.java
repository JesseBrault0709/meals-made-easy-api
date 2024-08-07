package app.mealsmadeeasy.api.security;

import java.time.LocalDateTime;

public interface AuthToken {
    String getToken();
    long getLifetime();
    LocalDateTime getExpires();
}
