package app.mealsmadeeasy.api.matchers;

import app.mealsmadeeasy.api.recipe.Recipe;
import app.mealsmadeeasy.api.recipe.star.RecipeStar;
import app.mealsmadeeasy.api.user.User;

public final class Matchers {

    public static ContainsRecipesMatcher containsRecipes(Recipe... expected) {
        return new ContainsRecipesMatcher(expected);
    }

    public static ContainsStarsMatcher containsStars(RecipeStar... expected) {
        return new ContainsStarsMatcher(expected);
    }

    public static IsRecipeMatcher isRecipe(Recipe expected) {
        return new IsRecipeMatcher(expected);
    }

    public static IsUserMatcher isUser(User expected) {
        return new IsUserMatcher(expected);
    }

    private Matchers() {}

}
