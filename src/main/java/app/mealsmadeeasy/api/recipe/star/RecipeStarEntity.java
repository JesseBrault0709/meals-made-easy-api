package app.mealsmadeeasy.api.recipe.star;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity(name = "RecipeStar")
public final class RecipeStarEntity implements RecipeStar {

    @EmbeddedId
    private RecipeStarId id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime date = LocalDateTime.now();

    public RecipeStarId getId() {
        return this.id;
    }

    public void setId(RecipeStarId id) {
        this.id = id;
    }

    @Override
    public LocalDateTime getDate() {
        return this.date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "RecipeStarEntity(" + this.id + ")";
    }

}
