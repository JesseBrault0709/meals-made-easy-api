package app.mealsmadeeasy.api.auth;

public interface AuthService {
    LoginDetails login(String username, String password) throws LoginException;
    void logout(String refreshToken);
    LoginDetails refresh(String refreshToken) throws LoginException;
}
