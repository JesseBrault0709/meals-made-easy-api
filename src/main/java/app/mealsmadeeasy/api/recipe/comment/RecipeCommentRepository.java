package app.mealsmadeeasy.api.recipe.comment;

import app.mealsmadeeasy.api.recipe.RecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeCommentRepository extends JpaRepository<RecipeCommentEntity, Long> {
    void deleteAllByRecipe(RecipeEntity recipe);
}
