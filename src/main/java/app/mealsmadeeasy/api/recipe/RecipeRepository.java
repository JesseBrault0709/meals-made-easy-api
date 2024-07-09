package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.user.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<RecipeEntity, Long> {

    List<RecipeEntity> findAllByIsPublicIsTrue();
    List<RecipeEntity> findAllByViewersContaining(UserEntity viewer);
    List<RecipeEntity> findAllByOwner(UserEntity owner);

    @Query("SELECT r FROM Recipe r WHERE size(r.stars) >= ?1 AND r.isPublic")
    List<RecipeEntity> findAllPublicByStarsGreaterThanEqual(long stars);

    @Query("SELECT r FROM Recipe r WHERE size(r.stars) >= ?1 AND (r.isPublic OR ?2 MEMBER OF r.viewers)")
    List<RecipeEntity> findAllViewableByStarsGreaterThanEqual(long stars, UserEntity viewer);

    @Query("SELECT r FROM Recipe r WHERE r.id = ?1")
    @EntityGraph(attributePaths = { "viewers" })
    RecipeEntity getByIdWithViewers(long id);

    @Query("SELECT r FROM Recipe r WHERE r.id = ?1")
    @EntityGraph(attributePaths = { "stars" })
    Optional<RecipeEntity> findByIdWithStars(long id);

    @Query("SELECT size(r.stars) FROM Recipe r WHERE r.id = ?1")
    int getStarCount(long recipeId);

    @Query("SELECT size(r.viewers) FROM Recipe r WHERE r.id = ?1")
    int getViewerCount(long recipeId);

}
