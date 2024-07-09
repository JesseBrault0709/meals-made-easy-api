package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.user.User;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("recipeSecurity")
public class RecipeSecurityImpl implements RecipeSecurity {

    private final RecipeRepository recipeRepository;

    public RecipeSecurityImpl(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public boolean isOwner(Recipe recipe, User user) {
        return recipe.getOwner() != null && recipe.getOwner().getId().equals(user.getId());
    }

    @Override
    public boolean isOwner(long recipeId, User user) throws RecipeException {
        final Recipe recipe = this.recipeRepository.findById(recipeId).orElseThrow(() -> new RecipeException(
                RecipeException.Type.INVALID_ID,
                "No such Recipe with id " + recipeId
        ));
        return this.isOwner(recipe, user);
    }

    @Override
    public boolean isViewableBy(Recipe recipe, User user) {
        if (Objects.equals(recipe.getOwner().getId(), user.getId())) {
            return true;
        } else {
            final RecipeEntity withViewers = this.recipeRepository.getByIdWithViewers(recipe.getId());
            for (final User viewer : withViewers.getViewers()) {
                if (viewer.getId() != null && viewer.getId().equals(user.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

}
