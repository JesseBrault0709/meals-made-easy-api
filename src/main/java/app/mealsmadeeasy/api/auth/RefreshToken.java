package app.mealsmadeeasy.api.auth;

import app.mealsmadeeasy.api.security.AuthToken;

import java.time.LocalDateTime;

public interface RefreshToken extends AuthToken {
    LocalDateTime getIssued();
    boolean isRevoked();
}
