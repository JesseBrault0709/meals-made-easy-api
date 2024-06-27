package app.mealsmadeeasy.api.recipe.star;

import app.mealsmadeeasy.api.recipe.RecipeEntity;
import app.mealsmadeeasy.api.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeStarRepository extends JpaRepository<RecipeStarEntity, Long> {
    List<RecipeStarEntity> findAllByOwner(UserEntity user);
    long countAllByOwner(UserEntity user);
    Optional<RecipeStarEntity> findByOwnerAndRecipe(UserEntity user, RecipeEntity recipe);
}
