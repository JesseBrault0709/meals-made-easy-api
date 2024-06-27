package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.recipe.comment.RecipeComment;
import app.mealsmadeeasy.api.user.User;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Set;

public interface Recipe {
    Long getId();
    LocalDateTime getCreated();
    @Nullable LocalDateTime getModified();
    String getTitle();
    String getRawText();
    User getOwner();
    Set<User> getStarGazers();
    boolean isPublic();
    Set<User> getViewers();
    Set<RecipeComment> getComments();
}
