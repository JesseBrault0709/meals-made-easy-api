package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.image.S3ImageEntity;
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
    private LocalDateTime created;

    private LocalDateTime modified;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String title;

    @Nullable
    private Integer preparationTime;

    @Nullable
    private Integer cookingTime;

    @Nullable
    private Integer totalTime;

    @Lob
    @Column(name = "raw_text", columnDefinition = "TEXT", nullable = false)
    @Basic(fetch = FetchType.LAZY)
    private String rawText;

    @Lob
    @Column(name = "cached_rendered_text", columnDefinition = "TEXT")
    @Basic(fetch = FetchType.LAZY)
    private String cachedRenderedText;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @OneToMany
    @JoinColumn(name = "recipeId")
    private Set<RecipeStarEntity> stars = new HashSet<>();

    @OneToMany(mappedBy = "recipe")
    private Set<RecipeCommentEntity> comments = new HashSet<>();

    @Column(nullable = false)
    private Boolean isPublic = false;

    @ManyToMany
    private Set<UserEntity> viewers = new HashSet<>();

    @ManyToOne
    private S3ImageEntity mainImage;

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
    public String getSlug() {
        return this.slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public @Nullable Integer getPreparationTime() {
        return this.preparationTime;
    }

    public void setPreparationTime(@Nullable Integer preparationTime) {
        this.preparationTime = preparationTime;
    }

    @Override
    public @Nullable Integer getCookingTime() {
        return this.cookingTime;
    }

    public void setCookingTime(@Nullable Integer cookingTime) {
        this.cookingTime = cookingTime;
    }

    @Override
    public @Nullable Integer getTotalTime() {
        return this.totalTime;
    }

    public void setTotalTime(@Nullable Integer totalTime) {
        this.totalTime = totalTime;
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

    @Override
    public S3ImageEntity getMainImage() {
        return this.mainImage;
    }

    public void setMainImage(S3ImageEntity image) {
        this.mainImage = image;
    }

}
