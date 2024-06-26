package app.mealsmadeeasy.api.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public final class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(
            String username,
            String email,
            String rawPassword,
            Set<UserGrantedAuthority> authorities
    ) throws UserCreateException {
        if (this.userRepository.existsByUsername(username)) {
            throw new UserCreateException(
                    UserCreateException.Type.USERNAME_TAKEN,
                    "Username " + username + " is taken."
            );
        }
        if (this.userRepository.existsByEmail(email)) {
            throw new UserCreateException(UserCreateException.Type.EMAIL_TAKEN, "Email " + email + " is taken.");
        }
        final UserEntity draft = UserEntity.getDefaultDraft();
        draft.setUsername(username);
        draft.setEmail(email);
        draft.setPassword(this.passwordEncoder.encode(rawPassword));
        draft.addAuthorities(authorities);
        return this.userRepository.save(draft);
    }

    @Override
    public User updateUser(User user) {
        return this.userRepository.save((UserEntity) user);
    }

    @Override
    public void deleteUser(User user) {
        this.userRepository.delete((UserEntity) user);
    }

    @Override
    public void deleteUser(String username) {
        this.userRepository.deleteByUsername(username);
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return !this.userRepository.existsByUsername(username);
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return !this.userRepository.existsByEmail(email);
    }

}
