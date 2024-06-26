package app.mealsmadeeasy.api.user;

public class UserCreateException extends Exception {

    public enum Type {
        USERNAME_TAKEN, EMAIL_TAKEN, BAD_PASSWORD
    }

    private final Type type;

    public UserCreateException(Type type, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    public UserCreateException(Type type, String message) {
        super(message);
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

}
