package app.mealsmadeeasy.api.recipe.star;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class RecipeStarId {

    @Column(nullable = false)
    private String ownerUsername;

    @Column(nullable = false)
    private Long recipeId;

    public String getOwnerUsername() {
        return this.ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public Long getRecipeId() {
        return this.recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof RecipeStarId other) {
            return this.recipeId.equals(other.recipeId) && this.ownerUsername.equals(other.ownerUsername);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.recipeId, this.ownerUsername);
    }

    @Override
    public String toString() {
        return "RecipeStarId(" + this.recipeId + ", " + this.ownerUsername + ")";
    }

}
