package app.mealsmadeeasy.api.recipe.star;

import app.mealsmadeeasy.api.recipe.RecipeEntity;
import app.mealsmadeeasy.api.user.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity(name = "RecipeStar")
public final class RecipeStarEntity implements RecipeStar {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false, updatable = false)
    private UserEntity owner;

    @Column(nullable = false, updatable = false)
    private LocalDateTime date = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false, updatable = false)
    private RecipeEntity recipe;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public UserEntity getOwner() {
        return this.owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    @Override
    public LocalDateTime getDate() {
        return this.date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public RecipeEntity getRecipe() {
        return this.recipe;
    }

    public void setRecipe(RecipeEntity recipe) {
        this.recipe = recipe;
    }

}
