package app.mealsmadeeasy.api.recipe.comment;

import app.mealsmadeeasy.api.recipe.RecipeException;
import app.mealsmadeeasy.api.user.User;

public interface RecipeCommentService {
    RecipeComment create(long recipeId, User owner, RecipeCommentCreateSpec spec) throws RecipeException;
    RecipeComment get(long commentId, User viewer) throws RecipeException;
    RecipeComment update(long commentId, User viewer, RecipeCommentUpdateSpec spec) throws RecipeException;
    void delete(long commentId, User modifier) throws RecipeException;
}
