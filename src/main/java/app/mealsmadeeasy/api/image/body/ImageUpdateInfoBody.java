package app.mealsmadeeasy.api.image.body;

import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ImageUpdateInfoBody {

    private @Nullable String alt;
    private @Nullable String caption;
    private @Nullable Boolean isPublic;
    private @Nullable Set<String> viewersToAdd;
    private @Nullable Set<String> viewersToRemove;
    private @Nullable Boolean clearAllViewers;

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

    public @Nullable Set<String> getViewersToAdd() {
        return this.viewersToAdd;
    }

    public void setViewersToAdd(@Nullable Set<String> viewersToAdd) {
        this.viewersToAdd = viewersToAdd;
    }

    public @Nullable Set<String> getViewersToRemove() {
        return this.viewersToRemove;
    }

    public void setViewersToRemove(@Nullable Set<String> viewersToRemove) {
        this.viewersToRemove = viewersToRemove;
    }

    public @Nullable Boolean getClearAllViewers() {
        return this.clearAllViewers;
    }

    public void setClearAllViewers(@Nullable Boolean clearAllViewers) {
        this.clearAllViewers = clearAllViewers;
    }

}
