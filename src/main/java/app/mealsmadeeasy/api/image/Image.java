package app.mealsmadeeasy.api.image;

import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserEntity;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Set;

public interface Image {
    Long getId();
    LocalDateTime getCreated();
    @Nullable LocalDateTime getModified();
    String getUserFilename();
    String getMimeType();
    @Nullable String getAlt();
    @Nullable String getCaption();
    String getObjectName();
    String getInternalUrl();
    User getOwner();
    boolean isPublic();
    Set<UserEntity> getViewers();
}
