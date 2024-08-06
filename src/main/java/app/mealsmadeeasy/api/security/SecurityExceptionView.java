package app.mealsmadeeasy.api.security;

public class SecurityExceptionView {

    public enum Action {
        LOGIN, REFRESH
    }

    private final int status;
    private final Action action;
    private final String message;

    public SecurityExceptionView(int status, Action action, String message) {
        this.status = status;
        this.action = action;
        this.message = message;
    }

    public int getStatus() {
        return this.status;
    }

    public Action getAction() {
        return this.action;
    }

    public String getMessage() {
        return this.message;
    }

}
