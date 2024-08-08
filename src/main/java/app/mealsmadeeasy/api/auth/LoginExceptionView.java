package app.mealsmadeeasy.api.auth;

public class LoginExceptionView {

    private final LoginExceptionReason reason;
    private final String message;

    public LoginExceptionView(LoginExceptionReason reason, String message) {
        this.reason = reason;
        this.message = message;
    }

    public LoginExceptionReason getReason() {
        return this.reason;
    }

    public String getMessage() {
        return this.message;
    }

}
