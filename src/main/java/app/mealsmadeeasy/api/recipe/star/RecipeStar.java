package app.mealsmadeeasy.api.recipe.star;

import app.mealsmadeeasy.api.recipe.Recipe;
import app.mealsmadeeasy.api.user.User;

import java.time.LocalDateTime;

public interface RecipeStar {
    User getOwner();
    LocalDateTime getDate();
    Recipe getRecipe();
}
