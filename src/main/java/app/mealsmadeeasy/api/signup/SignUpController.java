package app.mealsmadeeasy.api.signup;

import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserCreateException;
import app.mealsmadeeasy.api.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/sign-up")
public final class SignUpController {

    private final UserService userService;

    public SignUpController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Object>> checkUsername(@RequestBody Map<String, Object> body) {
        final boolean usernameAvailable = this.userService.isUsernameAvailable((String) body.get("username"));
        final Map<String, Object> result = Map.of("isAvailable", usernameAvailable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestBody Map<String, Object> body) {
        final boolean emailAvailable = this.userService.isEmailAvailable((String) body.get("email"));
        final Map<String, Object> result = Map.of("isAvailable", emailAvailable);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> signUp(@RequestBody SignUpBody body) {
        final User created;
        try {
            created = this.userService.createUser(body.getUsername(), body.getEmail(), body.getPassword());
        } catch (UserCreateException userCreateException) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", Map.of(
                            "type", userCreateException.getType().toString(),
                            "message", userCreateException.getMessage()
                    )
            ));
        }
        final Map<String, Object> view = Map.of("username", created.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(view);
    }

}
