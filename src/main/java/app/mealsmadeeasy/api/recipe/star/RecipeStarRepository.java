package app.mealsmadeeasy.api.recipe.star;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RecipeStarRepository extends JpaRepository<RecipeStarEntity, Long> {

    @Query("SELECT star FROM RecipeStar star WHERE star.id.recipeId = ?1 AND star.id.ownerUsername = ?2")
    Optional<RecipeStarEntity> findByRecipeIdAndOwnerUsername(Long recipeId, String username);

    @Query("DELETE FROM RecipeStar star WHERE star.id.recipeId = ?1 AND star.id.ownerUsername = ?2")
    void deleteByRecipeIdAndOwnerUsername(Long recipeId, String username);

}
