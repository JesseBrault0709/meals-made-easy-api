package app.mealsmadeeasy.api.user;

import java.util.Set;

public interface UserService {

    User createUser(String username, String email, String rawPassword, Set<UserGrantedAuthority> authorities)
            throws UserCreateException;

    default User createUser(String username, String email, String rawPassword) throws UserCreateException {
        return this.createUser(username, email, rawPassword, Set.of());
    }

    User updateUser(User user);
    void deleteUser(User user);
    void deleteUser(String username);

    boolean isUsernameAvailable(String username);
    boolean isEmailAvailable(String email);

}
