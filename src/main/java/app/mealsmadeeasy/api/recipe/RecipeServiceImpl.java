package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.recipe.comment.RecipeComment;
import app.mealsmadeeasy.api.recipe.comment.RecipeCommentEntity;
import app.mealsmadeeasy.api.recipe.comment.RecipeCommentRepository;
import app.mealsmadeeasy.api.recipe.star.RecipeStar;
import app.mealsmadeeasy.api.recipe.star.RecipeStarEntity;
import app.mealsmadeeasy.api.recipe.star.RecipeStarRepository;
import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserEntity;
import app.mealsmadeeasy.api.user.UserRepository;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public final class RecipeServiceImpl implements RecipeService {

    private static String renderAndCleanMarkdown(String rawText) {
        final var parser = Parser.builder().build();
        final var node = parser.parse(rawText);
        final var htmlRenderer = HtmlRenderer.builder().build();
        final String unsafeHtml = htmlRenderer.render(node);
        return Jsoup.clean(unsafeHtml, Safelist.relaxed());
    }

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final RecipeStarRepository recipeStarRepository;
    private final RecipeCommentRepository recipeCommentRepository;

    public RecipeServiceImpl(
            RecipeRepository recipeRepository,
            UserRepository userRepository,
            RecipeStarRepository recipeStarRepository,
            RecipeCommentRepository recipeCommentRepository
    ) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.recipeStarRepository = recipeStarRepository;
        this.recipeCommentRepository = recipeCommentRepository;
    }

    @Override
    public Recipe create(String ownerUsername, String title, String rawText) throws RecipeException {
        final RecipeEntity draft = new RecipeEntity();
        final UserEntity userEntity = this.userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new RecipeException(
                        RecipeException.Type.INVALID_OWNER_USERNAME,
                        "No such ownerUsername " + ownerUsername
                ));
        draft.setOwner(userEntity);
        draft.setTitle(title);
        draft.setRawText(rawText);
        return this.recipeRepository.save(draft);
    }

    @Override
    public Recipe getById(long id) throws RecipeException {
        return this.recipeRepository.findById(id).orElseThrow(() -> new RecipeException(
                RecipeException.Type.INVALID_ID,
                "No such recipe for id " + id
        ));
    }

    @Override
    public List<Recipe> getByMinimumStars(long minimumStars) {
        return List.copyOf(this.recipeRepository.findAllByStarsGreaterThanEqual(minimumStars));
    }

    @Override
    public List<Recipe> getPublicRecipes() {
        return List.copyOf(this.recipeRepository.findAllByIsPublicIsTrue());
    }

    @Override
    public List<Recipe> getRecipesViewableBy(User user) {
        return List.copyOf(this.recipeRepository.findAllByViewersContaining((UserEntity) user));
    }

    @Override
    public List<Recipe> getRecipesOwnedBy(User user) {
        return List.copyOf(this.recipeRepository.findAllByOwner((UserEntity) user));
    }

    @Override
    public String getRenderedMarkdown(Recipe recipe) {
        RecipeEntity entity = (RecipeEntity) recipe;
        if (entity.getCachedRenderedText() == null) {
            entity.setCachedRenderedText(renderAndCleanMarkdown(entity.getRawText()));
            entity = this.recipeRepository.save(entity);
        }
        return entity.getCachedRenderedText();
    }

    @Override
    public Recipe updateRawText(Recipe recipe, String newRawText) {
        final RecipeEntity entity = (RecipeEntity) recipe;
        entity.setCachedRenderedText(null);
        entity.setRawText(newRawText);
        return this.recipeRepository.save(entity);
    }

    @Override
    public Recipe updateOwner(Recipe recipe, String newOwnerUsername) throws RecipeException {
        final RecipeEntity entity = (RecipeEntity) recipe;
        final UserEntity newOwner = this.userRepository.findByUsername(newOwnerUsername)
                .orElseThrow(() -> new RecipeException(
                        RecipeException.Type.INVALID_OWNER_USERNAME,
                        "No such username: " + newOwnerUsername
                ));
        entity.setOwner(newOwner);
        return this.recipeRepository.save(entity);
    }

    @Override
    public RecipeStar addStar(Recipe recipe, User giver) {
        final RecipeEntity entity = (RecipeEntity) recipe;
        final RecipeStarEntity star = new RecipeStarEntity();
        star.setOwner((UserEntity) giver);
        star.setRecipe(entity);
        return this.recipeStarRepository.save(star);
    }

    @Override
    public void deleteStar(RecipeStar recipeStar) {
        this.recipeStarRepository.delete((RecipeStarEntity) recipeStar);
    }

    @Override
    public void deleteStarByUser(Recipe recipe, User giver) throws RecipeException {
        final RecipeStarEntity star = this.recipeStarRepository.findByOwnerAndRecipe(
                        (UserEntity) giver,
                        (RecipeEntity) recipe
                ).orElseThrow(() -> new RecipeException(
                        RecipeException.Type.INVALID_STAR,
                        "No such star for user " + giver.getUsername() + " and recipe " + recipe.getId()
                ));
        this.recipeStarRepository.delete(star);
    }

    @Override
    public Recipe setPublic(Recipe recipe, boolean isPublic) {
        final RecipeEntity entity = (RecipeEntity) recipe;
        entity.setPublic(isPublic);
        return this.recipeRepository.save(entity);
    }

    @Override
    public Recipe addViewer(Recipe recipe, User user) {
        final RecipeEntity entity = (RecipeEntity) recipe;
        final Set<UserEntity> viewers = new HashSet<>(entity.getViewerEntities());
        viewers.add((UserEntity) user);
        entity.setViewers(viewers);
        return this.recipeRepository.save(entity);
    }

    @Override
    public Recipe removeViewer(Recipe recipe, User user) {
        final RecipeEntity entity = (RecipeEntity) recipe;
        final Set<UserEntity> viewers = new HashSet<>(entity.getViewerEntities());
        viewers.remove((UserEntity) user);
        entity.setViewers(viewers);
        return this.recipeRepository.save(entity);
    }

    @Override
    public Recipe clearViewers(Recipe recipe) {
        final RecipeEntity entity = (RecipeEntity) recipe;
        entity.setViewers(new HashSet<>());
        return this.recipeRepository.save(entity);
    }

    @Override
    public RecipeComment getCommentById(long id) throws RecipeException {
        return this.recipeCommentRepository.findById(id)
                .orElseThrow(() -> new RecipeException(
                        RecipeException.Type.INVALID_ID,
                        "No such RecipeComment for id " + id
                ));
    }

    @Override
    public RecipeComment addComment(Recipe recipe, String rawCommentText, User commenter) {
        final RecipeCommentEntity draft = new RecipeCommentEntity();
        draft.setRawText(rawCommentText);
        draft.setOwner((UserEntity) commenter);
        return this.recipeCommentRepository.save(draft);
    }

    @Override
    public RecipeComment updateComment(RecipeComment comment, String newRawCommentText) {
        final RecipeCommentEntity entity = (RecipeCommentEntity) comment;
        entity.setCachedRenderedText(null);
        entity.setRawText(newRawCommentText);
        return this.recipeCommentRepository.save(entity);
    }

    @Override
    public String getRenderedMarkdown(RecipeComment recipeComment) {
        RecipeCommentEntity entity = (RecipeCommentEntity) recipeComment;
        if (entity.getCachedRenderedText() == null) {
            entity.setCachedRenderedText(renderAndCleanMarkdown(entity.getRawText()));
            entity = this.recipeCommentRepository.save(entity);
        }
        return entity.getCachedRenderedText();
    }

    @Override
    public void deleteComment(RecipeComment comment) {
        this.recipeCommentRepository.delete((RecipeCommentEntity) comment);
    }

    @Override
    public Recipe clearComments(Recipe recipe) {
        this.recipeCommentRepository.deleteAllByRecipe((RecipeEntity) recipe);
        return this.recipeRepository.getReferenceById(recipe.getId());
    }

    @Override
    public void deleteRecipe(Recipe recipe) {
        this.recipeRepository.delete((RecipeEntity) recipe);
    }

    @Override
    public void deleteById(long id) {
        this.recipeRepository.deleteById(id);
    }

}
