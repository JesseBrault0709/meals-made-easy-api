package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.recipe.comment.RecipeComment;
import app.mealsmadeeasy.api.recipe.comment.RecipeCommentEntity;
import app.mealsmadeeasy.api.recipe.star.RecipeStar;
import app.mealsmadeeasy.api.recipe.star.RecipeStarEntity;
import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserEntity;
import jakarta.persistence.*;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "Recipe")
public final class RecipeEntity implements Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime created = LocalDateTime.now();

    private LocalDateTime modified;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String rawText;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String cachedRenderedText;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @OneToMany(mappedBy = "recipe")
    private Set<RecipeStarEntity> stars = new HashSet<>();

    @OneToMany(mappedBy = "recipe")
    private Set<RecipeCommentEntity> comments = new HashSet<>();

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

    public void setModified(@Nullable LocalDateTime modified) {
        this.modified = modified;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getRawText() {
        return this.rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public String getCachedRenderedText() {
        return this.cachedRenderedText;
    }

    public void setCachedRenderedText(String cachedRenderedText) {
        this.cachedRenderedText = cachedRenderedText;
    }

    @Override
    public UserEntity getOwner() {
        return this.owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    @Override
    public boolean isPublic() {
        return this.isPublic;
    }

    public void setPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    @Override
    public Set<User> getViewers() {
        return Set.copyOf(this.viewers);
    }

    public Set<UserEntity> getViewerEntities() {
        return this.viewers;
    }

    public void setViewers(Set<UserEntity> viewers) {
        this.viewers = viewers;
    }

    @Override
    public Set<RecipeStar> getStars() {
        return Set.copyOf(this.stars);
    }

    public Set<RecipeStarEntity> getStarEntities() {
        return this.stars;
    }

    public void setStarEntities(Set<RecipeStarEntity> starGazers) {
        this.stars = starGazers;
    }

    @Override
    public Set<RecipeComment> getComments() {
        return Set.copyOf(this.comments);
    }

    public Set<RecipeCommentEntity> getCommentEntities() {
        return this.comments;
    }

    public void setComments(Set<RecipeCommentEntity> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "RecipeEntity(" + this.id + ", " + this.title + ")";
    }

}
