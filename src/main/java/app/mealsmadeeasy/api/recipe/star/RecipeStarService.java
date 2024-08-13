package app.mealsmadeeasy.api.recipe.star;

import app.mealsmadeeasy.api.recipe.RecipeException;
import app.mealsmadeeasy.api.user.User;

import java.util.Optional;

public interface RecipeStarService {
    RecipeStar create(long recipeId, String ownerUsername);
    RecipeStar create(String recipeOwnerUsername, String recipeSlug, User starer) throws RecipeException;

    Optional<RecipeStar> find(String recipeOwnerUsername, String recipeSlug, User starer) throws RecipeException;

    void delete(long recipeId, String ownerUsername);
    void delete(String recipeOwnerUsername, String recipeSlug, User starer) throws RecipeException;
}
