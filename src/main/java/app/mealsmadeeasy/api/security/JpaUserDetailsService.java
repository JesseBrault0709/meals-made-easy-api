package app.mealsmadeeasy.api.security;

import app.mealsmadeeasy.api.user.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface JpaUserDetailsService extends UserDetailsService {
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(String username);
    void deleteUser(User user);
}
