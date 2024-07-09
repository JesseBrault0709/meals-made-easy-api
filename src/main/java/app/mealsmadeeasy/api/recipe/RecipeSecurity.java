package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.user.User;

public interface RecipeSecurity {
    boolean isOwner(Recipe recipe, User user);
    boolean isOwner(long recipeId, User user) throws RecipeException;
    boolean isViewableBy(Recipe recipe, User user);
}
