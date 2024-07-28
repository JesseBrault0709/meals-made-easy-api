package app.mealsmadeeasy.api.recipe.comment;

import app.mealsmadeeasy.api.recipe.RecipeEntity;
import app.mealsmadeeasy.api.recipe.RecipeException;
import app.mealsmadeeasy.api.recipe.RecipeRepository;
import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static java.util.Objects.requireNonNull;

@Service
public class RecipeCommentServiceImpl implements RecipeCommentService {

    private final RecipeCommentRepository recipeCommentRepository;
    private final RecipeRepository recipeRepository;

    public RecipeCommentServiceImpl(
            RecipeCommentRepository recipeCommentRepository,
            RecipeRepository recipeRepository
    ) {
        this.recipeCommentRepository = recipeCommentRepository;
        this.recipeRepository = recipeRepository;
    }

    @Override
    public RecipeComment create(long recipeId, User owner, RecipeCommentCreateSpec spec) throws RecipeException {
        requireNonNull(owner);
        final RecipeCommentEntity draft = new RecipeCommentEntity();
        draft.setCreated(LocalDateTime.now());
        draft.setRawText(spec.getRawText());
        draft.setOwner((UserEntity) owner);
        final RecipeEntity recipe = this.recipeRepository.findById(recipeId).orElseThrow(() -> new RecipeException(
                RecipeException.Type.INVALID_ID, "No such Recipe for id: " + recipeId
        ));
        draft.setRecipe(recipe);
        return this.recipeCommentRepository.save(draft);
    }

    @PostAuthorize("@recipeSecurity.isViewableBy(returnObject.recipe, #viewer)")
    private RecipeCommentEntity loadCommentEntity(long commentId, User viewer) throws RecipeException {
        return this.recipeCommentRepository.findById(commentId).orElseThrow(() -> new RecipeException(
                RecipeException.Type.INVALID_COMMENT_ID, "No such RecipeComment for id: " + commentId
        ));
    }

    @Override
    public RecipeComment get(long commentId, User viewer) throws RecipeException {
        return this.loadCommentEntity(commentId, viewer);
    }

    @Override
    public RecipeComment update(long commentId, User viewer, RecipeCommentUpdateSpec spec) throws RecipeException {
        final RecipeCommentEntity entity = this.loadCommentEntity(commentId, viewer);
        entity.setRawText(spec.getRawText());
        return this.recipeCommentRepository.save(entity);
    }

    @PostAuthorize("@recipeSecurity.isOwner(returnObject.recipe, #modifier)")
    private RecipeCommentEntity loadForDelete(long commentId, User modifier) throws RecipeException {
        return this.recipeCommentRepository.findById(commentId).orElseThrow(() -> new RecipeException(
                RecipeException.Type.INVALID_COMMENT_ID, "No such RecipeComment for id: " + commentId
        ));
    }

    @Override
    public void delete(long commentId, User modifier) throws RecipeException {
        final RecipeCommentEntity entityToDelete = this.loadForDelete(commentId, modifier);
        this.recipeCommentRepository.delete(entityToDelete);
    }

}
