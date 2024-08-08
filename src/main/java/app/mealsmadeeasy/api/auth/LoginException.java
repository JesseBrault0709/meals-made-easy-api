package app.mealsmadeeasy.api.auth;

public final class LoginException extends Exception {

    private final LoginExceptionReason reason;

    public LoginException(LoginExceptionReason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public LoginException(LoginExceptionReason reason, Throwable cause) {
        super(cause);
        this.reason = reason;
    }

    public LoginExceptionReason getReason() {
        return this.reason;
    }

}
