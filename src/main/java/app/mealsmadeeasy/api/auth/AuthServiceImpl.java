package app.mealsmadeeasy.api.auth;

import app.mealsmadeeasy.api.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public final class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public LoginDetails login(String username, String password) throws LoginException {
        try {
            this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    username,
                    password
            ));
            return new LoginDetails(
                    username,
                    this.jwtService.generateAccessToken(username),
                    this.jwtService.generateRefreshToken(username)
            );
        } catch (Exception e) {
            throw new LoginException(e);
        }
    }

}
