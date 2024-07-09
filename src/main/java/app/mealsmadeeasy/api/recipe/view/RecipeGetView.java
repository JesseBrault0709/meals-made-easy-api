package app.mealsmadeeasy.api.recipe.view;

public final class RecipeGetView {

    private final long id;
    private final String title;

    public RecipeGetView(long id, String title) {
        this.id = id;
        this.title = title;
    }

    public long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

}
