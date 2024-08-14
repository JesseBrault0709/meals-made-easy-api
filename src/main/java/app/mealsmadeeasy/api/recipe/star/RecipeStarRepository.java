package app.mealsmadeeasy.api.recipe.star;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RecipeStarRepository extends JpaRepository<RecipeStarEntity, Long> {

    @Query("SELECT star FROM RecipeStar star WHERE star.id.recipeId = ?1 AND star.id.ownerUsername = ?2")
    Optional<RecipeStarEntity> findByRecipeIdAndOwnerUsername(Long recipeId, String username);

    @Query("SELECT count(rs) > 0 FROM RecipeStar rs WHERE rs.id.recipeId = ?1 AND rs.id.ownerUsername = ?2")
    boolean isStarer(long recipeId, String username);

    @Modifying
    @Transactional
    @Query("DELETE FROM RecipeStar star WHERE star.id.recipeId = ?1 AND star.id.ownerUsername = ?2")
    void deleteByRecipeIdAndOwnerUsername(Long recipeId, String username);

}
