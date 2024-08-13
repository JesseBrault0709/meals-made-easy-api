package app.mealsmadeeasy.api.recipe.star;

import app.mealsmadeeasy.api.recipe.RecipeException;
import app.mealsmadeeasy.api.user.User;

public interface RecipeStarService {
    RecipeStar create(long recipeId, String ownerUsername);
    RecipeStar create(String recipeOwnerUsername, String recipeSlug, User starer) throws RecipeException;
    RecipeStar get(long recipeId, String ownerUsername) throws RecipeException;
    void delete(long recipeId, String ownerUsername);
}
