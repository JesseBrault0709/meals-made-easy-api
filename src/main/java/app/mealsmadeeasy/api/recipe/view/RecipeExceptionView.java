package app.mealsmadeeasy.api.recipe.view;

public final class RecipeExceptionView {

    private final String type;
    private final String message;

    public RecipeExceptionView(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getType() {
        return this.type;
    }

    public String getMessage() {
        return this.message;
    }

}
