package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.recipe.comment.RecipeComment;
import app.mealsmadeeasy.api.recipe.star.RecipeStar;
import app.mealsmadeeasy.api.recipe.view.RecipeInfoView;
import app.mealsmadeeasy.api.recipe.view.RecipePageView;
import app.mealsmadeeasy.api.user.User;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface RecipeService {

    Recipe create(String ownerUsername, String title, String rawText) throws RecipeException;
    Recipe create(User user, String title, String rawText);

    Recipe getById(long id) throws RecipeException;
    Recipe getById(long id, User viewer) throws RecipeException;

    Recipe getByIdWithStars(long id) throws RecipeException;
    Recipe getByIdWithStars(long id, User viewer) throws RecipeException;

    RecipePageView getPageViewById(long id, @Nullable User viewer) throws RecipeException;
    Slice<RecipeInfoView> getInfoViewsViewableBy(Pageable pageable, @Nullable User viewer);

    List<Recipe> getByMinimumStars(long minimumStars);
    List<Recipe> getByMinimumStars(long minimumStars, User viewer);

    List<Recipe> getPublicRecipes();
    List<Recipe> getRecipesViewableBy(User user);
    List<Recipe> getRecipesOwnedBy(User user);

    String getRenderedMarkdown(Recipe recipe, User viewer);

    Recipe updateRawText(Recipe recipe, User owner, String newRawText);

    Recipe updateOwner(Recipe recipe, User oldOwner, User newOwner) throws RecipeException;

    RecipeStar addStar(Recipe recipe, User giver) throws RecipeException;
    void deleteStarByUser(Recipe recipe, User giver) throws RecipeException;
    void deleteStar(RecipeStar recipeStar);
    int getStarCount(Recipe recipe, @Nullable User viewer);

    Recipe setPublic(Recipe recipe, User owner, boolean isPublic);

    Recipe addViewer(Recipe recipe, User user);
    Recipe removeViewer(Recipe recipe, User user);
    Recipe clearViewers(Recipe recipe);
    int getViewerCount(Recipe recipe, @Nullable User viewer);

    RecipeComment getCommentById(long id) throws RecipeException;
    RecipeComment addComment(Recipe recipe, String rawCommentText, User commenter);
    RecipeComment updateComment(RecipeComment comment, String newRawCommentText);
    String getRenderedMarkdown(RecipeComment recipeComment);
    void deleteComment(RecipeComment comment);
    Recipe clearComments(Recipe recipe);

    void deleteRecipe(Recipe recipe, User owner);
    void deleteById(long id, User owner);

}
