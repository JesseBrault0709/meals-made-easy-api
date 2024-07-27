package app.mealsmadeeasy.api.util;

public final class AccessDeniedView {

    private final int statusCode;
    private final String message;

    public AccessDeniedView(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getMessage() {
        return this.message;
    }

}
