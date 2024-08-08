package app.mealsmadeeasy.api.auth;

import app.mealsmadeeasy.api.security.AuthToken;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public final class AuthController {

    private static ResponseCookie getRefreshTokenCookie(@Nullable String token, long maxAge) {
        final ResponseCookie.ResponseCookieBuilder b = ResponseCookie.from("refresh-token")
                .httpOnly(true)
                .secure(true)
                .maxAge(maxAge)
                .path("/");
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
                loginDetails.getUsername(),
                loginDetails.getAccessToken().getToken(),
                loginDetails.getAccessToken().getExpires()
        );
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(loginView);
    }

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<LoginExceptionView> onLoginException(LoginException ex) {
        final LoginExceptionView loginExceptionView = new LoginExceptionView(ex.getReason(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginExceptionView);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginView> login(@RequestBody LoginBody loginBody) throws LoginException {
        final LoginDetails loginDetails = this.authService.login(loginBody.getUsername(), loginBody.getPassword());
        return this.getLoginViewResponseEntity(loginDetails);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginView> refresh(
            @CookieValue(value = "refresh-token", required = false) @Nullable String oldRefreshToken
    ) throws LoginException {
        final LoginDetails loginDetails = this.authService.refresh(oldRefreshToken);
        return this.getLoginViewResponseEntity(loginDetails);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(value = "refresh-token", required = false) @Nullable String refreshToken
    ) {
        if (refreshToken != null) {
            this.authService.logout(refreshToken);
        }
        final ResponseCookie deleteRefreshCookie = getRefreshTokenCookie(null, 0);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteRefreshCookie.toString())
                .build();
    }

}
