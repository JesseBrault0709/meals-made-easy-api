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

    @Query("SELECT r FROM Recipe r WHERE size(r.stars) >= ?1")
    List<RecipeEntity> findAllByStarsGreaterThanEqual(long stars);

    @Query("SELECT r FROM Recipe r WHERE r.id = ?1")
    @EntityGraph(attributePaths = { "viewers" })
    RecipeEntity getByIdWithViewers(long id);

    @Query("SELECT r FROM Recipe r WHERE r.id = ?1")
    @EntityGraph(attributePaths = { "stars" })
    Optional<RecipeEntity> findByIdWithStars(long id);

}
