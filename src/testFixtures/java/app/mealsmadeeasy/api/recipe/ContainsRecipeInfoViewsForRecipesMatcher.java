package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.matchers.ContainsItemsMatcher;
import app.mealsmadeeasy.api.recipe.view.RecipeInfoView;

import java.util.List;

public class ContainsRecipeInfoViewsForRecipesMatcher extends ContainsItemsMatcher<RecipeInfoView, Recipe, Long> {

    public static ContainsRecipeInfoViewsForRecipesMatcher containsRecipeInfoViewsForRecipes(Recipe... expected) {
        return new ContainsRecipeInfoViewsForRecipesMatcher(List.of(expected));
    }

    private ContainsRecipeInfoViewsForRecipesMatcher(List<Recipe> expected) {
        super(
                expected,
                o -> o instanceof RecipeInfoView,
                RecipeInfoView::getId,
                Recipe::getId
        );
    }

}
