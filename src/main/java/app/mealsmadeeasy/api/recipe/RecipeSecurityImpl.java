package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.user.User;
import org.jetbrains.annotations.Nullable;
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
    public boolean isOwner(String username, String slug, @Nullable User user) throws RecipeException {
        final Recipe recipe = this.recipeRepository.findByOwnerUsernameAndSlug(username, slug).orElseThrow(
                () -> new RecipeException(
                        RecipeException.Type.INVALID_USERNAME_OR_SLUG,
                        "No such Recipe for username " + username + " and slug " + slug
                )
        );
        return this.isOwner(recipe, user);
    }

    @Override
    public boolean isViewableBy(Recipe recipe, @Nullable User user) throws RecipeException {
        if (recipe.isPublic()) {
            // public recipe
            return true;
        } else if (user == null) {
            // a non-public recipe with no principal
            return false;
        } else if (Objects.equals(recipe.getOwner().getId(), user.getId())) {
            // is owner
            return true;
        } else {
            // check if viewer
            final RecipeEntity withViewers = this.recipeRepository.findByIdWithViewers(recipe.getId())
                    .orElseThrow(() -> new RecipeException(
                            RecipeException.Type.INVALID_ID, "No such Recipe with id: " + recipe.getId()
                    ));
            for (final User viewer : withViewers.getViewers()) {
                if (viewer.getId() != null && viewer.getId().equals(user.getId())) {
                    return true;
                }
            }
        }
        // non-public recipe and not viewer
        return false;
    }

    @Override
    public boolean isViewableBy(String ownerUsername, String slug, @Nullable User user) throws RecipeException {
        final Recipe recipe = this.recipeRepository.findByOwnerUsernameAndSlug(ownerUsername, slug)
                .orElseThrow(() -> new RecipeException(
                        RecipeException.Type.INVALID_USERNAME_OR_SLUG,
                        "No such Recipe for username " + ownerUsername + " and slug: " + slug
                ));
        return this.isViewableBy(recipe, user);
    }

    @Override
    public boolean isViewableBy(long recipeId, @Nullable User user) throws RecipeException {
        final Recipe recipe = this.recipeRepository.findById(recipeId).orElseThrow(() -> new RecipeException(
                RecipeException.Type.INVALID_ID,
                "No such Recipe with id: " + recipeId
        ));
        return this.isViewableBy(recipe, user);
    }

}
