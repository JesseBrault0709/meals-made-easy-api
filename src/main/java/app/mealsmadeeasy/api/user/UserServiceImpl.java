package app.mealsmadeeasy.api.user;

import app.mealsmadeeasy.api.security.JpaUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public final class UserServiceImpl implements UserService {

    private final JpaUserDetailsService jpaUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(JpaUserDetailsService jpaUserDetailsService, PasswordEncoder passwordEncoder) {
        this.jpaUserDetailsService = jpaUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(
            String username,
            String email,
            String rawPassword,
            Set<UserGrantedAuthority> authorities
    ) {
        final UserEntity draft = UserEntity.getDefaultDraft();
        draft.setUsername(username);
        draft.setEmail(email);
        draft.setPassword(this.passwordEncoder.encode(rawPassword));
        draft.addAuthorities(authorities);
        return this.jpaUserDetailsService.createUser(draft);
    }

    @Override
    public User updateUser(User user) {
        return this.jpaUserDetailsService.updateUser(user);
    }

    @Override
    public void deleteUser(User user) {
        this.jpaUserDetailsService.deleteUser(user);
    }

    @Override
    public void deleteUser(String username) {
        this.jpaUserDetailsService.deleteUser(username);
    }

}
