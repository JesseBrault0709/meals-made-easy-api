package app.mealsmadeeasy.api.recipe.view;

import app.mealsmadeeasy.api.image.view.ImageView;

import java.time.LocalDateTime;

public final class RecipeInfoView {

    private long id;
    private LocalDateTime updated;
    private String title;
    private long ownerId;
    private String ownerUsername;
    private boolean isPublic;
    private int starCount;
    private ImageView mainImage;

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

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
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

    public ImageView getMainImage() {
        return this.mainImage;
    }

    public void setMainImage(ImageView mainImage) {
        this.mainImage = mainImage;
    }

}
