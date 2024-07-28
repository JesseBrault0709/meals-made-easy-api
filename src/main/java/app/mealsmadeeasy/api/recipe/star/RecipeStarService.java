package app.mealsmadeeasy.api.recipe.star;

import app.mealsmadeeasy.api.recipe.RecipeException;

public interface RecipeStarService {
    RecipeStar create(long recipeId, String ownerUsername);
    RecipeStar get(long recipeId, String ownerUsername) throws RecipeException;
    void delete(long recipeId, String ownerUsername);
}
