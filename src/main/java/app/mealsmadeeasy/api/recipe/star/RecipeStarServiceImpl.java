package app.mealsmadeeasy.api.recipe.star;

import app.mealsmadeeasy.api.recipe.RecipeException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RecipeStarServiceImpl implements RecipeStarService {

    private final RecipeStarRepository recipeStarRepository;

    public RecipeStarServiceImpl(RecipeStarRepository recipeStarRepository) {
        this.recipeStarRepository = recipeStarRepository;
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

}
