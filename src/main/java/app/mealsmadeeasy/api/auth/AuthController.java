package app.mealsmadeeasy.api.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public final class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginView> login(@RequestBody LoginBody loginBody) {
        try {
            final LoginDetails loginDetails = this.authService.login(loginBody.getUsername(), loginBody.getPassword());
            final String serializedToken = loginDetails.getRefreshToken().getToken();
            final ResponseCookie refreshCookie = ResponseCookie.from("refresh-token", serializedToken)
                    .httpOnly(true)
                    .secure(true)
                    .maxAge(loginDetails.getRefreshToken().getLifetime())
                    .build();
            final LoginView loginView = new LoginView(
                    loginDetails.getUsername(), loginDetails.getAccessToken().getToken()
            );
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(loginView);
        } catch (LoginException loginException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue("refresh-token") String refreshToken) {
        final ResponseCookie deleteRefreshCookie = ResponseCookie.from("refresh-token")
                .httpOnly(true)
                .secure(true)
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteRefreshCookie.toString())
                .build();
    }

}
