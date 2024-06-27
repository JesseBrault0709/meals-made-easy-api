package app.mealsmadeeasy.api.recipe.comment;

import app.mealsmadeeasy.api.recipe.RecipeEntity;
import app.mealsmadeeasy.api.user.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity(name = "RecipeComment")
public final class RecipeCommentEntity implements RecipeComment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created = LocalDateTime.now();

    private LocalDateTime modified;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String rawText;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String cachedRenderedText;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false, updatable = false)
    private UserEntity owner;

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false, updatable = false)
    private RecipeEntity recipe;

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
    public LocalDateTime getModified() {
        return this.modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
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
    public RecipeEntity getRecipe() {
        return this.recipe;
    }

    public void setRecipe(RecipeEntity recipe) {
        this.recipe = recipe;
    }

}
