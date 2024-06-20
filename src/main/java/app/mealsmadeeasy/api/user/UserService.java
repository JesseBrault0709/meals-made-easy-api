package app.mealsmadeeasy.api.user;

import java.util.Set;

public interface UserService {
    User createUser(String username, String email, String rawPassword, Set<UserGrantedAuthority> authorities);
    User updateUser(User user);
    void deleteUser(User user);
    void deleteUser(String username);
}
