package app.mealsmadeeasy.api.recipe.star;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RecipeStarRepository extends JpaRepository<RecipeStarEntity, Long> {

    @Query("SELECT star FROM RecipeStar star WHERE star.id.recipeId = ?1 AND star.id.ownerUsername = ?2")
    Optional<RecipeStarEntity> findByRecipeIdAndOwnerUsername(Long recipeId, String username);

    @Query("SELECT count(rs) > 0 FROM RecipeStar rs, Recipe r WHERE r.owner.username = ?1 AND r.slug = ?2 AND r.id = rs.id.recipeId AND rs.id.ownerUsername = ?3")
    boolean isStarer(String ownerUsername, String slug, String viewerUsername);

    @Modifying
    @Transactional
    @Query("DELETE FROM RecipeStar star WHERE star.id.recipeId = ?1 AND star.id.ownerUsername = ?2")
    void deleteByRecipeIdAndOwnerUsername(Long recipeId, String username);

}
