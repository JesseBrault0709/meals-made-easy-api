package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.user.User;

public interface RecipeSecurity {
    boolean isOwner(Recipe recipe, User user);
    boolean isViewableBy(Recipe recipe, User user);
}
