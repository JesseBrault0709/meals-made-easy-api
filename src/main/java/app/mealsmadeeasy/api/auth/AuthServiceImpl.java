package app.mealsmadeeasy.api.auth;

import app.mealsmadeeasy.api.jwt.JwtService;
import app.mealsmadeeasy.api.user.UserEntity;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public final class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshTokenLifetime;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            RefreshTokenRepository refreshTokenRepository,
            @Value("${app.mealsmadeeasy.api.security.refresh-token-lifetime}") Long refreshTokenLifetime
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenLifetime = refreshTokenLifetime;
    }

    private RefreshToken createRefreshToken(UserEntity principal) {
        final RefreshTokenEntity refreshTokenDraft = new RefreshTokenEntity();
        refreshTokenDraft.setToken(UUID.randomUUID().toString());
        refreshTokenDraft.setIssued(LocalDateTime.now());
        refreshTokenDraft.setExpiration(LocalDateTime.now().plusSeconds(this.refreshTokenLifetime));
        refreshTokenDraft.setOwner(principal);
        return this.refreshTokenRepository.save(refreshTokenDraft);
    }

    @Override
    public LoginDetails login(String username, String password) throws LoginException {
        try {
            final Authentication authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    username,
                    password
            ));
            final UserEntity principal = (UserEntity) authentication.getPrincipal();
            return new LoginDetails(
                    username,
                    this.jwtService.generateAccessToken(username),
                    this.createRefreshToken(principal)
            );
        } catch (AuthenticationException e) {
            throw new LoginException(e);
        }
    }

    @Override
    public void logout(String refreshToken) {
        this.refreshTokenRepository.findByToken(refreshToken).ifPresent(this.refreshTokenRepository::delete);
    }

    @Override
    public LoginDetails refresh(String refreshToken) throws LoginException {
        try {
            final RefreshTokenEntity old = this.refreshTokenRepository.findByToken(refreshToken)
                    .orElseThrow(() -> new LoginException("No such refresh-token: " + refreshToken));
            if (old.isRevoked()) {
                throw new LoginException("RefreshToken is revoked.");
            }
            if (old.getExpires().isBefore(LocalDateTime.now())) {
                throw new LoginException("RefreshToken is expired.");
            }
            final UserEntity principal = old.getOwner();
            this.refreshTokenRepository.delete(old);

            final String username = principal.getUsername();
            return new LoginDetails(
                    username,
                    this.jwtService.generateAccessToken(username),
                    this.createRefreshToken(principal)
            );
        } catch (JwtException e) {
            throw new LoginException(e);
        }
    }

}
