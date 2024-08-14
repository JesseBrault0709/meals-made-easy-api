package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.recipe.spec.RecipeCreateSpec;
import app.mealsmadeeasy.api.recipe.spec.RecipeUpdateSpec;
import app.mealsmadeeasy.api.recipe.view.FullRecipeView;
import app.mealsmadeeasy.api.recipe.view.RecipeInfoView;
import app.mealsmadeeasy.api.user.User;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface RecipeService {

    Recipe create(@Nullable User owner, RecipeCreateSpec spec);

    Recipe getById(long id, @Nullable User viewer) throws RecipeException;
    Recipe getByIdWithStars(long id, @Nullable User viewer) throws RecipeException;
    Recipe getByUsernameAndSlug(String username, String slug, @Nullable User viewer) throws RecipeException;

    FullRecipeView getFullViewById(long id, @Nullable User viewer) throws RecipeException;
    FullRecipeView getFullViewByUsernameAndSlug(String username, String slug, @Nullable User viewer) throws RecipeException;

    Slice<RecipeInfoView> getInfoViewsViewableBy(Pageable pageable, @Nullable User viewer);
    List<Recipe> getByMinimumStars(long minimumStars, @Nullable User viewer);
    List<Recipe> getPublicRecipes();
    List<Recipe> getRecipesViewableBy(User viewer);
    List<Recipe> getRecipesOwnedBy(User owner);

    Recipe update(long id, RecipeUpdateSpec spec, User modifier) throws RecipeException;

    Recipe addViewer(long id, User modifier, User viewer) throws RecipeException;
    Recipe removeViewer(long id, User modifier, User viewer) throws RecipeException;
    Recipe clearAllViewers(long id, User modifier) throws RecipeException;

    void deleteRecipe(long id, User modifier);

    @Contract("_, _, null -> null")
    @Nullable Boolean isStarer(String username, String slug, @Nullable User viewer);

    @Contract("_, _, null -> null")
    @Nullable Boolean isOwner(String username, String slug, @Nullable User viewer);

}
