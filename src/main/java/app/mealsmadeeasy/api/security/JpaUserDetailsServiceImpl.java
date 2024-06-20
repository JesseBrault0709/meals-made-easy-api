package app.mealsmadeeasy.api.security;

import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserEntity;
import app.mealsmadeeasy.api.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public final class JpaUserDetailsServiceImpl implements JpaUserDetailsService {

    private final UserRepository userRepository;

    public JpaUserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        return this.userRepository.save((UserEntity) user);
    }

    @Override
    public User updateUser(User user) {
        return this.userRepository.save((UserEntity) user);
    }

    @Override
    public void deleteUser(String username) {
        final UserEntity user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No such User with username: " + username));
        this.userRepository.delete(user);
    }

    @Override
    public void deleteUser(User user) {
        this.userRepository.delete((UserEntity) user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No such User with username: " + username));
    }

}
