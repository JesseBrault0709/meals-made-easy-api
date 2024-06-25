package app.mealsmadeeasy.api.auth;

import app.mealsmadeeasy.api.security.AuthToken;

import java.time.LocalDateTime;

public interface RefreshToken extends AuthToken {
    LocalDateTime getIssued();
    LocalDateTime getExpiration();
    boolean isRevoked();
}
