package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.matchers.ContainsItemsMatcher;

import java.util.List;

public final class ContainsRecipesMatcher extends ContainsItemsMatcher<Recipe, Recipe, Long> {

    public static ContainsRecipesMatcher containsRecipes(Recipe... expected) {
        return new ContainsRecipesMatcher(expected);
    }

    private ContainsRecipesMatcher(Recipe[] allExpected) {
        super(List.of(allExpected), o -> o instanceof Recipe, Recipe::getId, Recipe::getId);
    }

}
