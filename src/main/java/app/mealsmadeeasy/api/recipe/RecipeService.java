package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.recipe.comment.RecipeComment;
import app.mealsmadeeasy.api.recipe.star.RecipeStar;
import app.mealsmadeeasy.api.user.User;

import java.util.List;

public interface RecipeService {

    Recipe create(String ownerUsername, String title, String rawText) throws RecipeException;

    Recipe getById(long id) throws RecipeException;
    List<Recipe> getByMinimumStars(long minimumStars);
    List<Recipe> getPublicRecipes();
    List<Recipe> getRecipesViewableBy(User user);
    List<Recipe> getRecipesOwnedBy(User user);

    String getRenderedMarkdown(Recipe recipe);

    Recipe updateRawText(Recipe recipe, String newRawText);

    Recipe updateOwner(Recipe recipe, String newOwnerUsername) throws RecipeException;

    RecipeStar addStar(Recipe recipe, User giver);
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