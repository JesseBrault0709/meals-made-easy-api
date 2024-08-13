package app.mealsmadeeasy.api.recipe.star;

import app.mealsmadeeasy.api.recipe.Recipe;
import app.mealsmadeeasy.api.recipe.RecipeException;
import app.mealsmadeeasy.api.recipe.RecipeService;
import app.mealsmadeeasy.api.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RecipeStarServiceImpl implements RecipeStarService {

    private final RecipeStarRepository recipeStarRepository;
    private final RecipeService recipeService;

    public RecipeStarServiceImpl(RecipeStarRepository recipeStarRepository, RecipeService recipeService) {
        this.recipeStarRepository = recipeStarRepository;
        this.recipeService = recipeService;
    }

    @Override
    public RecipeStar create(long recipeId, String ownerUsername) {
        final RecipeStarEntity draft = new RecipeStarEntity();
        final RecipeStarId id = new RecipeStarId();
        id.setRecipeId(recipeId);
        id.setOwnerUsername(ownerUsername);
        draft.setId(id);
        draft.setDate(LocalDateTime.now());
        return this.recipeStarRepository.save(draft);
    }

    @Override
    public RecipeStar create(String recipeOwnerUsername, String recipeSlug, User starer) throws RecipeException {
        final Recipe recipe = this.recipeService.getByUsernameAndSlug(recipeOwnerUsername, recipeSlug, starer);
        final Optional<RecipeStarEntity> existing = this.recipeStarRepository.findByRecipeIdAndOwnerUsername(
                recipe.getId(),
                starer.getUsername()
        );
        if (existing.isPresent()) {
            return existing.get();
        }
        return this.create(recipe.getId(), starer.getUsername());
    }

    @Override
    public RecipeStar get(long recipeId, String ownerUsername) throws RecipeException {
        return this.recipeStarRepository.findByRecipeIdAndOwnerUsername(recipeId, ownerUsername).orElseThrow(
                () -> new RecipeException(
                        RecipeException.Type.INVALID_ID,
                        "No such RecipeStar for recipeId: " + recipeId + " and ownerUsername: " + ownerUsername
                )
        );
    }

    @Override
    public void delete(long recipeId, String ownerUsername) {
        this.recipeStarRepository.deleteByRecipeIdAndOwnerUsername(recipeId, ownerUsername);
    }

    @Override
    public void delete(String recipeOwnerUsername, String recipeSlug, User starer) throws RecipeException {
        final Recipe recipe = this.recipeService.getByUsernameAndSlug(recipeOwnerUsername, recipeSlug, starer);
        this.delete(recipe.getId(), starer.getUsername());
    }

}
