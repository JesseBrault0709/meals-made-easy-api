package app.mealsmadeeasy.api.security;

public class SecurityExceptionView {

    private final int status;
    private final String message;

    public SecurityExceptionView(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message;
    }

}
