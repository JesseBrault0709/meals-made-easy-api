package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.matchers.ContainsItemsMatcher;
import app.mealsmadeeasy.api.recipe.star.RecipeStar;
import app.mealsmadeeasy.api.recipe.star.RecipeStarEntity;
import app.mealsmadeeasy.api.recipe.star.RecipeStarId;

import java.util.List;
import java.util.Objects;

public class ContainsRecipeStarsMatcher extends ContainsItemsMatcher<RecipeStar, RecipeStarId> {

    public static ContainsRecipeStarsMatcher containsStars(RecipeStar... expected) {
        return new ContainsRecipeStarsMatcher(expected);
    }

    private ContainsRecipeStarsMatcher(RecipeStar[] allExpected) {
        super(
                List.of(allExpected),
                o -> o instanceof RecipeStar,
                recipeStar -> ((RecipeStarEntity) recipeStar).getId(),
                (id0, id1) -> Objects.equals(id0.getRecipeId(), id1.getRecipeId())
                        && Objects.equals(id0.getOwnerUsername(), id1.getOwnerUsername())
        );
    }

}
