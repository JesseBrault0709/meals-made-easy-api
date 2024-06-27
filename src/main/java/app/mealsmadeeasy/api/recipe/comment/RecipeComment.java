package app.mealsmadeeasy.api.recipe.comment;

import app.mealsmadeeasy.api.recipe.Recipe;
import app.mealsmadeeasy.api.user.User;

import java.time.LocalDateTime;

public interface RecipeComment {
    LocalDateTime getCreated();
    LocalDateTime getModified();
    String getRawText();
    User getOwner();
    Recipe getRecipe();
}
