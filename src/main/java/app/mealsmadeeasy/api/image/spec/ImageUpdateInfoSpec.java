package app.mealsmadeeasy.api.image.spec;

import app.mealsmadeeasy.api.user.User;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class ImageUpdateInfoSpec extends ImageCreateInfoSpec {

    private @Nullable Set<User> viewersToRemove;
    private @Nullable Boolean clearAllViewers;

    public @Nullable Set<User> getViewersToRemove() {
        return this.viewersToRemove;
    }

    public void setViewersToRemove(@Nullable Set<User> viewersToRemove) {
        this.viewersToRemove = viewersToRemove;
    }

    public @Nullable Boolean getClearAllViewers() {
        return this.clearAllViewers;
    }

    public void setClearAllViewers(@Nullable Boolean clearAllViewers) {
        this.clearAllViewers = clearAllViewers;
    }

}
