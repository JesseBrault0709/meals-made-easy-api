package app.mealsmadeeasy.api.recipe;

public class RecipeException extends Exception {

    public enum Type {
        INVALID_OWNER_USERNAME, INVALID_STAR, NOT_VIEWABLE, INVALID_ID
    }

    private final Type type;

    public RecipeException(Type type, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    public RecipeException(Type type, String message) {
        super(message);
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

}
