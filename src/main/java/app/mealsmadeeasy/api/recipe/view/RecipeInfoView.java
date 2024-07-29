package app.mealsmadeeasy.api.recipe.view;

import app.mealsmadeeasy.api.image.view.ImageView;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

public final class RecipeInfoView {

    private long id;
    private LocalDateTime updated;
    private String slug;
    private String title;
    private String ownerUsername;
    private boolean isPublic;
    private int starCount;
    private @Nullable ImageView mainImage;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getUpdated() {
        return this.updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public String getSlug() {
        return this.slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOwnerUsername() {
        return this.ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public boolean isPublic() {
        return this.isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public int getStarCount() {
        return this.starCount;
    }

    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }

    public @Nullable ImageView getMainImage() {
        return this.mainImage;
    }

    public void setMainImage(@Nullable ImageView mainImage) {
        this.mainImage = mainImage;
    }

}
