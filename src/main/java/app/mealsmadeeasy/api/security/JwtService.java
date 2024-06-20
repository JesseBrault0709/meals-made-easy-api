package app.mealsmadeeasy.api.security;

public interface JwtService {
    AuthToken generateAccessToken(String username);
    AuthToken generateRefreshToken(String username);
}
