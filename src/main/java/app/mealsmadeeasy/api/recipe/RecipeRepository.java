package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.user.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<RecipeEntity, Long> {

    List<RecipeEntity> findAllByIsPublicIsTrue();

    List<RecipeEntity> findAllByViewersContaining(UserEntity viewer);

    List<RecipeEntity> findAllByOwner(UserEntity owner);

    @Query("SELECT r from Recipe r WHERE r.owner.username = ?1 AND r.slug = ?2")
    Optional<RecipeEntity> findByOwnerUsernameAndSlug(String ownerUsername, String slug);

    @Query("SELECT r FROM Recipe r WHERE size(r.stars) >= ?1 AND (r.isPublic OR ?2 MEMBER OF r.viewers)")
    List<RecipeEntity> findAllViewableByStarsGreaterThanEqual(long stars, UserEntity viewer);

    @Query("SELECT r FROM Recipe r WHERE r.id = ?1")
    @EntityGraph(attributePaths = { "viewers" })
    Optional<RecipeEntity> findByIdWithViewers(long id);

    @Query("SELECT r FROM Recipe r WHERE r.id = ?1")
    @EntityGraph(attributePaths = { "stars" })
    Optional<RecipeEntity> findByIdWithStars(long id);

    @Query("SELECT size(r.stars) FROM Recipe r WHERE r.id = ?1")
    int getStarCount(long recipeId);

    @Query("SELECT size(r.viewers) FROM Recipe r WHERE r.id = ?1")
    int getViewerCount(long recipeId);

    @Query("SELECT r FROM Recipe r WHERE r.isPublic OR ?1 MEMBER OF r.viewers")
    Slice<RecipeEntity> findAllViewableBy(UserEntity viewer, Pageable pageable);

}
