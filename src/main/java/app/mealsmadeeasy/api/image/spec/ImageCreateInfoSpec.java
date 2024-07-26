package app.mealsmadeeasy.api.image.spec;

import app.mealsmadeeasy.api.user.User;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class ImageCreateInfoSpec {

    private @Nullable String alt;
    private @Nullable String caption;
    private @Nullable Boolean isPublic;
    private Set<User> viewersToAdd = new HashSet<>();

    public @Nullable String getAlt() {
        return this.alt;
    }

    public void setAlt(@Nullable String alt) {
        this.alt = alt;
    }

    public @Nullable String getCaption() {
        return this.caption;
    }

    public void setCaption(@Nullable String caption) {
        this.caption = caption;
    }

    public @Nullable Boolean getPublic() {
        return this.isPublic;
    }

    public void setPublic(@Nullable Boolean aPublic) {
        isPublic = aPublic;
    }

    public Set<User> getViewersToAdd() {
        return this.viewersToAdd;
    }

    public void setViewersToAdd(Set<User> viewersToAdd) {
        this.viewersToAdd = viewersToAdd;
    }

}
