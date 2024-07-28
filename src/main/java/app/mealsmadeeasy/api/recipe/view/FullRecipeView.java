package app.mealsmadeeasy.api.recipe.view;

import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

public class FullRecipeView {

    private long id;
    private LocalDateTime created;
    private LocalDateTime modified;
    private String title;
    private String text;
    private long ownerId;
    private String ownerUsername;
    private int starCount;
    private int viewerCount;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getCreated() {
        return this.created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getModified() {
        return this.modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public @Nullable String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
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

    public int getStarCount() {
        return this.starCount;
    }

    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }

    public int getViewerCount() {
        return this.viewerCount;
    }

    public void setViewerCount(int viewerCount) {
        this.viewerCount = viewerCount;
    }

}
