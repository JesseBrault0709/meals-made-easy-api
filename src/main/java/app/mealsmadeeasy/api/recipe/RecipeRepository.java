package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecipeRepository extends JpaRepository<RecipeEntity, Long> {
    List<RecipeEntity> findAllByIsPublicIsTrue();
    List<RecipeEntity> findAllByViewersContaining(UserEntity viewer);
    List<RecipeEntity> findAllByOwner(UserEntity owner);

    @Query("SELECT r FROM Recipe r WHERE size(r.starGazers) > ?1")
    List<RecipeEntity> findAllByStarGazersGreaterThanEqual(long stars);
}
