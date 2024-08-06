package app.mealsmadeeasy.api.auth;

import app.mealsmadeeasy.api.security.AuthToken;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public final class AuthController {

    private static ResponseCookie getRefreshTokenCookie(String token, long maxAge) {
        final ResponseCookie.ResponseCookieBuilder b = ResponseCookie.from("refresh-token")
                .httpOnly(true)
                .secure(true)
                .maxAge(maxAge);
        if (token != null) {
            b.value(token);
        }
        return b.build();
    }

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    private ResponseEntity<LoginView> getLoginViewResponseEntity(LoginDetails loginDetails) {
        final AuthToken refreshToken = loginDetails.getRefreshToken();
        final ResponseCookie refreshCookie = getRefreshTokenCookie(
                refreshToken.getToken(),
                refreshToken.getLifetime()
        );
        final var loginView = new LoginView(
                loginDetails.getUsername(), loginDetails.getAccessToken().getToken()
        );
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(loginView);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginView> login(@RequestBody LoginBody loginBody) {
        try {
            final LoginDetails loginDetails = this.authService.login(loginBody.getUsername(), loginBody.getPassword());
            return this.getLoginViewResponseEntity(loginDetails);
        } catch (LoginException loginException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginView> refresh(
            @CookieValue(value = "refresh-token") String oldRefreshToken
    ) {
        try {
            final LoginDetails loginDetails = this.authService.refresh(oldRefreshToken);
            return this.getLoginViewResponseEntity(loginDetails);
        } catch (LoginException loginException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(value = "refresh-token", required = false) String refreshToken) {
        if (refreshToken != null) {
            this.authService.logout(refreshToken);
        }
        final ResponseCookie deleteRefreshCookie = getRefreshTokenCookie(null, 0);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteRefreshCookie.toString())
                .build();
    }

}
