package app.mealsmadeeasy.api.recipe.star;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

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
    public String toString() {
        return "RecipeStarId(" + this.recipeId + ", " + this.ownerUsername + ")";
    }

}
