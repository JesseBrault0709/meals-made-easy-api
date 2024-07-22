package app.mealsmadeeasy.api.image;

import app.mealsmadeeasy.api.user.User;
import org.jetbrains.annotations.Nullable;

public interface ImageSecurity {
    boolean isViewableBy(Image image, @Nullable User viewer);
    boolean isOwner(Image image, @Nullable User user);
}
