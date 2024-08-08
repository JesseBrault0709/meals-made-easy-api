package app.mealsmadeeasy.api.auth;

import org.jetbrains.annotations.Nullable;

public interface AuthService {
    LoginDetails login(String username, String password) throws LoginException;
    void logout(String refreshToken);
    LoginDetails refresh(@Nullable String refreshToken) throws LoginException;
}
