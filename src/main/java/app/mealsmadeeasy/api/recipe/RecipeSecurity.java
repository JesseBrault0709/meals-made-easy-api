package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.user.User;
import org.jetbrains.annotations.Nullable;

public interface RecipeSecurity {
    boolean isOwner(Recipe recipe, User user);
    boolean isOwner(long recipeId, User user) throws RecipeException;
    boolean isViewableBy(Recipe recipe, @Nullable User user) throws RecipeException;
    boolean isViewableBy(long recipeId, @Nullable User user) throws RecipeException;
}
