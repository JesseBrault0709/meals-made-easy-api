package app.mealsmadeeasy.api.image;

import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserEntity;
import jakarta.persistence.*;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "Image")
public class S3ImageEntity implements Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime created = LocalDateTime.now();

    private LocalDateTime modified;

    @Column(nullable = false)
    private String userFilename;

    @Column(nullable = false)
    private String mimeType;

    private String alt;

    private String caption;

    @Column(nullable = false)
    private String objectName;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @Column(nullable = false)
    private Boolean isPublic = false;

    @ManyToMany
    private Set<UserEntity> viewers = new HashSet<>();

    @Override
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public LocalDateTime getCreated() {
        return this.created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @Override
    public @Nullable LocalDateTime getModified() {
        return this.modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    @Override
    public String getUserFilename() {
        return this.userFilename;
    }

    public void setUserFilename(String userFilename) {
        this.userFilename = userFilename;
    }

    @Override
    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public @Nullable String getAlt() {
        return this.alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    @Override
    public @Nullable String getCaption() {
        return this.caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getObjectName() {
        return this.objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    @Override
    public User getOwner() {
        return this.owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    @Override
    public boolean isPublic() {
        return this.isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    @Override
    public Set<UserEntity> getViewers() {
        return this.viewers;
    }

    public void setViewers(Set<UserEntity> viewers) {
        this.viewers = viewers;
    }

    @Override
    public String toString() {
        return "S3ImageEntity(" + this.id + ", " + this.userFilename + "," + this.objectName + ")";
    }

}
