package app.mealsmadeeasy.api.image.view;

import app.mealsmadeeasy.api.image.Image;
import app.mealsmadeeasy.api.user.view.UserInfoView;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class ImageView {

    public static ImageView from(Image image, String url, boolean includeViewers) {
        final ImageView view = new ImageView();
        view.setUrl(url);
        view.setCreated(image.getCreated());
        view.setModified(image.getModified());
        view.setFilename(image.getUserFilename());
        view.setMimeType(image.getMimeType());
        view.setAlt(image.getAlt());
        view.setCaption(image.getCaption());
        view.setOwner(UserInfoView.from(image.getOwner()));
        view.setIsPublic(image.isPublic());
        if (includeViewers) {
            view.setViewers(image.getViewers().stream()
                    .map(UserInfoView::from)
                    .collect(Collectors.toSet())
            );
        }
        return view;
    }

    private String url;
    private LocalDateTime created;
    private @Nullable LocalDateTime modified;
    private String filename;
    private String mimeType;
    private @Nullable String alt;
    private @Nullable String caption;
    private UserInfoView owner;
    private boolean isPublic;
    private @Nullable Set<UserInfoView> viewers;

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

    public @Nullable Set<UserInfoView> getViewers() {
        return this.viewers;
    }

    public void setViewers(@Nullable Set<UserInfoView> viewers) {
        this.viewers = viewers;
    }

}
