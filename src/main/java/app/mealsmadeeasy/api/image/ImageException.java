package app.mealsmadeeasy.api.image;

public class ImageException extends Exception {

    public enum Type {
        INVALID_ID, UNKNOWN_MIME_TYPE
    }

    private final Type type;

    public ImageException(Type type, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    public ImageException(Type type, String message) {
        super(message);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

}
