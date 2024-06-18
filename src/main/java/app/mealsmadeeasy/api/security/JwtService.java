package app.mealsmadeeasy.api.security;

public interface JwtService {
    String generateToken(String username);
}
