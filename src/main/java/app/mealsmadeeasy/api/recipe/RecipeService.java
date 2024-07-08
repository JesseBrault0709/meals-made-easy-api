package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.recipe.comment.RecipeComment;
import app.mealsmadeeasy.api.recipe.star.RecipeStar;
import app.mealsmadeeasy.api.user.User;

import java.util.List;

public interface RecipeService {

    Recipe create(String ownerUsername, String title, String rawText) throws RecipeException;
    Recipe create(User user, String title, String rawText);

    Recipe getById(long id) throws RecipeException;
    Recipe getById(long id, User viewer) throws RecipeException;

    Recipe getByIdWithStars(long id) throws RecipeException;
    Recipe getByIdWithStars(long id, User viewer) throws RecipeException;

    List<Recipe> getByMinimumStars(long minimumStars);
    List<Recipe> getByMinimumStars(long minimumStars, User viewer);

    List<Recipe> getPublicRecipes();
    List<Recipe> getRecipesViewableBy(User user);
    List<Recipe> getRecipesOwnedBy(User user);

    String getRenderedMarkdown(Recipe recipe);

    Recipe updateRawText(Recipe recipe, String newRawText);

    Recipe updateOwner(Recipe recipe, User oldOwner, User newOwner) throws RecipeException;

    RecipeStar addStar(Recipe recipe, User giver) throws RecipeException;
    void deleteStarByUser(Recipe recipe, User giver) throws RecipeException;
    void deleteStar(RecipeStar recipeStar);

    Recipe setPublic(Recipe recipe, boolean isPublic);

    Recipe addViewer(Recipe recipe, User user);
    Recipe removeViewer(Recipe recipe, User user);
    Recipe clearViewers(Recipe recipe);

    RecipeComment getCommentById(long id) throws RecipeException;
    RecipeComment addComment(Recipe recipe, String rawCommentText, User commenter);
    RecipeComment updateComment(RecipeComment comment, String newRawCommentText);
    String getRenderedMarkdown(RecipeComment recipeComment);
    void deleteComment(RecipeComment comment);
    Recipe clearComments(Recipe recipe);

    void deleteRecipe(Recipe recipe);
    void deleteById(long id);

}
