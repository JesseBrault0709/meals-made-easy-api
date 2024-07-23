package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.matchers.ContainsItemsMatcher;
import app.mealsmadeeasy.api.recipe.star.RecipeStar;

import java.util.List;

public class ContainsRecipeStarsMatcher extends ContainsItemsMatcher<RecipeStar, Long> {

    public static ContainsRecipeStarsMatcher containsStars(RecipeStar... expected) {
        return new ContainsRecipeStarsMatcher(expected);
    }

    private ContainsRecipeStarsMatcher(RecipeStar[] allExpected) {
        super(List.of(allExpected), o -> o instanceof RecipeStar, RecipeStar::getId);
    }

}
