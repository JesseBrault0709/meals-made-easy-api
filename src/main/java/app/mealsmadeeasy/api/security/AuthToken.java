package app.mealsmadeeasy.api.security;

public interface AuthToken {
    String getToken();
    long getLifetime();
}
