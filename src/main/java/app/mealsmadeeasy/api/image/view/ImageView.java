package app.mealsmadeeasy.api.image.view;

import app.mealsmadeeasy.api.user.view.UserInfoView;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Set;

// TODO: get rid of viewers, keep it only for owner view!
public class ImageView {

    private String url;
    private LocalDateTime created;
    private @Nullable LocalDateTime modified;
    private String filename;
    private String mimeType;
    private @Nullable String alt;
    private @Nullable String caption;
    private UserInfoView owner;
    private boolean isPublic;
    private Set<UserInfoView> viewers;

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getCreated() {
        return this.created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public @Nullable LocalDateTime getModified() {
        return this.modified;
    }

    public void setModified(@Nullable LocalDateTime modified) {
        this.modified = modified;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getAlt() {
        return this.alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getCaption() {
        return this.caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public UserInfoView getOwner() {
        return this.owner;
    }

    public void setOwner(UserInfoView owner) {
        this.owner = owner;
    }

    public boolean getIsPublic() {
        return this.isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Set<UserInfoView> getViewers() {
        return this.viewers;
    }

    public void setViewers(Set<UserInfoView> viewers) {
        this.viewers = viewers;
    }

}
