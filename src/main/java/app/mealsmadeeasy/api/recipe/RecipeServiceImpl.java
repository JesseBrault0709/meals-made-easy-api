package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.recipe.comment.RecipeComment;
import app.mealsmadeeasy.api.recipe.comment.RecipeCommentEntity;
import app.mealsmadeeasy.api.recipe.comment.RecipeCommentRepository;
import app.mealsmadeeasy.api.recipe.star.RecipeStar;
import app.mealsmadeeasy.api.recipe.star.RecipeStarEntity;
import app.mealsmadeeasy.api.recipe.star.RecipeStarRepository;
import app.mealsmadeeasy.api.recipe.view.RecipeInfoView;
import app.mealsmadeeasy.api.recipe.view.RecipePageView;
import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserEntity;
import app.mealsmadeeasy.api.user.UserRepository;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RecipeServiceImpl implements RecipeService {

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
    public Recipe create(User user, String title, String rawText) {
        final RecipeEntity draft = new RecipeEntity();
        draft.setOwner((UserEntity) user);
        draft.setTitle(title);
        draft.setRawText(rawText);
        return this.recipeRepository.save(draft);
    }

    @Override
    @PostAuthorize("returnObject.isPublic")
    public Recipe getById(long id) throws RecipeException {
        return this.recipeRepository.findById(id).orElseThrow(() -> new RecipeException(
                RecipeException.Type.INVALID_ID,
                "No such recipe for id " + id
        ));
    }

    @Override
    @PostAuthorize("@recipeSecurity.isViewableBy(returnObject, #viewer)")
    public Recipe getById(long id, User viewer) throws RecipeException {
        return this.recipeRepository.findById(id).orElseThrow(() -> new RecipeException(
                RecipeException.Type.INVALID_ID,
                "No such recipe for id " + id
        ));
    }

    @Override
    @PostAuthorize("returnObject.isPublic")
    public Recipe getByIdWithStars(long id) throws RecipeException {
        return this.recipeRepository.findByIdWithStars(id).orElseThrow(() -> new RecipeException(
                RecipeException.Type.INVALID_ID,
                "No such recipe for id " + id
        ));
    }

    @Override
    @PostAuthorize("@recipeSecurity.isViewableBy(returnObject, #viewer)")
    public Recipe getByIdWithStars(long id, User viewer) throws RecipeException {
        return this.recipeRepository.findByIdWithStars(id).orElseThrow(() -> new RecipeException(
                RecipeException.Type.INVALID_ID,
                "No such recipe for id " + id
        ));
    }

    @Override
    @PostAuthorize("@recipeSecurity.isViewableBy(#id, #viewer)")
    public RecipePageView getPageViewById(long id, @Nullable User viewer) throws RecipeException {
        final Recipe recipe = this.recipeRepository.getReferenceById(id);
        final RecipePageView view = new RecipePageView();
        view.setId(recipe.getId());
        view.setCreated(recipe.getCreated());
        view.setModified(recipe.getModified());
        view.setTitle(recipe.getTitle());
        view.setText(this.getRenderedMarkdown(recipe, viewer));
        view.setOwnerId(recipe.getOwner().getId());
        view.setOwnerUsername(recipe.getOwner().getUsername());
        view.setStarCount(this.getStarCount(recipe, viewer));
        view.setViewerCount(this.getViewerCount(recipe, viewer));
        return view;
    }

    @Override
    public Slice<RecipeInfoView> getInfoViewsViewableBy(Pageable pageable, @Nullable User viewer) {
        return this.recipeRepository.findAllViewableBy((UserEntity) viewer, pageable).map(entity -> {
            final RecipeInfoView view = new RecipeInfoView();
            view.setId(entity.getId());
            if (entity.getModified() != null) {
                view.setUpdated(entity.getModified());
            } else {
                view.setUpdated(entity.getCreated());
            }
            view.setTitle(entity.getTitle());
            view.setOwnerId(entity.getOwner().getId());
            view.setOwnerUsername(entity.getOwner().getUsername());
            view.setPublic(entity.isPublic());
            view.setStarCount(this.getStarCount(entity, viewer));
            return view;
        });
    }

    @Override
    public List<Recipe> getByMinimumStars(long minimumStars) {
        return List.copyOf(this.recipeRepository.findAllPublicByStarsGreaterThanEqual(minimumStars));
    }

    @Override
    public List<Recipe> getByMinimumStars(long minimumStars, User viewer) {
        return List.copyOf(
                this.recipeRepository.findAllViewableByStarsGreaterThanEqual(minimumStars, (UserEntity) viewer)
        );
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
    @PreAuthorize("@recipeSecurity.isViewableBy(#recipe, #viewer)")
    public String getRenderedMarkdown(Recipe recipe, User viewer) {
        RecipeEntity entity = (RecipeEntity) recipe;
        if (entity.getCachedRenderedText() == null) {
            entity.setCachedRenderedText(renderAndCleanMarkdown(entity.getRawText()));
            entity = this.recipeRepository.save(entity);
        }
        return entity.getCachedRenderedText();
    }

    @Override
    @PreAuthorize("@recipeSecurity.isOwner(#recipe, #owner)")
    public Recipe updateRawText(Recipe recipe, User owner, String newRawText) {
        final RecipeEntity entity = (RecipeEntity) recipe;
        entity.setCachedRenderedText(null);
        entity.setRawText(newRawText);
        return this.recipeRepository.save(entity);
    }

    @Override
    @PreAuthorize("@recipeSecurity.isOwner(#recipe, #oldOwner)")
    public Recipe updateOwner(Recipe recipe, User oldOwner, User newOwner) {
        final RecipeEntity entity = (RecipeEntity) recipe;
        entity.setOwner((UserEntity) newOwner);
        return this.recipeRepository.save(entity);
    }

    @Override
    @PreAuthorize("@recipeSecurity.isViewableBy(#recipe, #giver)")
    public RecipeStar addStar(Recipe recipe, User giver) {
        final RecipeStarEntity star = new RecipeStarEntity();
        star.setOwner((UserEntity) giver);
        star.setRecipe((RecipeEntity) recipe);
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
    @PreAuthorize("@recipeSecurity.isViewableBy(#recipe, #viewer)")
    public int getStarCount(Recipe recipe, @Nullable User viewer) {
        return this.recipeRepository.getStarCount(recipe.getId());
    }

    @Override
    @PreAuthorize("@recipeSecurity.isOwner(#recipe, #owner)")
    public Recipe setPublic(Recipe recipe, User owner, boolean isPublic) {
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
    @PreAuthorize("@recipeSecurity.isViewableBy(#recipe, #viewer)")
    public int getViewerCount(Recipe recipe, User viewer) {
        return this.recipeRepository.getViewerCount(recipe.getId());
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
    @PreAuthorize("@recipeSecurity.isOwner(#recipe, #owner)")
    public void deleteRecipe(Recipe recipe, User owner) {
        this.recipeRepository.delete((RecipeEntity) recipe);
    }

    @Override
    @PreAuthorize("@recipeSecurity.isOwner(#id, #owner)")
    public void deleteById(long id, User owner) {
        this.recipeRepository.deleteById(id);
    }

}
