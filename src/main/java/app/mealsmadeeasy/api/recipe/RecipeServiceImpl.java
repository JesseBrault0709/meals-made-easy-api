package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.image.Image;
import app.mealsmadeeasy.api.image.ImageService;
import app.mealsmadeeasy.api.image.S3ImageEntity;
import app.mealsmadeeasy.api.image.view.ImageView;
import app.mealsmadeeasy.api.recipe.spec.RecipeCreateSpec;
import app.mealsmadeeasy.api.recipe.spec.RecipeUpdateSpec;
import app.mealsmadeeasy.api.recipe.view.FullRecipeView;
import app.mealsmadeeasy.api.recipe.view.RecipeInfoView;
import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserEntity;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private final ImageService imageService;


    public RecipeServiceImpl(RecipeRepository recipeRepository, ImageService imageService) {
        this.recipeRepository = recipeRepository;
        this.imageService = imageService;
    }

    @Override
    public Recipe create(@Nullable User owner, RecipeCreateSpec spec) {
        if (owner == null) {
            throw new AccessDeniedException("Must be logged in.");
        }
        final RecipeEntity draft = new RecipeEntity();
        draft.setCreated(LocalDateTime.now());
        draft.setOwner((UserEntity) owner);
        draft.setSlug(spec.getSlug());
        draft.setTitle(spec.getTitle());
        draft.setRawText(spec.getRawText());
        draft.setMainImage((S3ImageEntity) spec.getMainImage());
        draft.setPublic(spec.isPublic());
        return this.recipeRepository.save(draft);
    }

    private RecipeEntity findRecipeEntity(long id) throws RecipeException {
        return this.recipeRepository.findById(id).orElseThrow(() -> new RecipeException(
                RecipeException.Type.INVALID_ID, "No such Recipe with id: " + id
        ));
    }

    @Override
    @PostAuthorize("@recipeSecurity.isViewableBy(returnObject, #viewer)")
    public Recipe getById(long id, User viewer) throws RecipeException {
        return this.findRecipeEntity(id);
    }

    @Override
    @PostAuthorize("@recipeSecurity.isViewableBy(returnObject, #viewer)")
    public Recipe getByIdWithStars(long id, @Nullable User viewer) throws RecipeException {
        return this.recipeRepository.findByIdWithStars(id).orElseThrow(() -> new RecipeException(
                RecipeException.Type.INVALID_ID,
                "No such Recipe with id: " + id
        ));
    }

    @Override
    @PostAuthorize("@recipeSecurity.isViewableBy(returnObject, #viewer)")
    public Recipe getByUsernameAndSlug(String username, String slug, @Nullable User viewer) throws RecipeException {
        return this.recipeRepository.findByOwnerUsernameAndSlug(username, slug).orElseThrow(() -> new RecipeException(
                RecipeException.Type.INVALID_USERNAME_OR_SLUG,
                "No such Recipe for username " + username + " and slug " + slug
        ));
    }

    private String getRenderedMarkdown(RecipeEntity entity) {
        if (entity.getCachedRenderedText() == null) {
            entity.setCachedRenderedText(renderAndCleanMarkdown(entity.getRawText()));
            entity = this.recipeRepository.save(entity);
        }
        return entity.getCachedRenderedText();
    }

    private int getStarCount(Recipe recipe) {
        return this.recipeRepository.getStarCount(recipe.getId());
    }

    private int getViewerCount(long recipeId) {
        return this.recipeRepository.getViewerCount(recipeId);
    }

    @Contract("null, _ -> null")
    private @Nullable ImageView getImageView(@Nullable Image image, @Nullable User viewer) {
        if (image != null) {
            return this.imageService.toImageView(image, viewer);
        } else {
            return null;
        }
    }

    private FullRecipeView getFullView(RecipeEntity recipe, @Nullable User viewer) {
        return FullRecipeView.from(
                recipe,
                this.getRenderedMarkdown(recipe),
                this.getStarCount(recipe),
                this.getViewerCount(recipe.getId()),
                this.getImageView(recipe.getMainImage(), viewer)
        );
    }

    private RecipeInfoView getInfoView(RecipeEntity recipe, @Nullable User viewer) {
        return RecipeInfoView.from(
                recipe,
                this.getStarCount(recipe),
                this.getImageView(recipe.getMainImage(), viewer)
        );
    }

    @Override
    @PreAuthorize("@recipeSecurity.isViewableBy(#id, #viewer)")
    public FullRecipeView getFullViewById(long id, @Nullable User viewer) throws RecipeException {
        final RecipeEntity recipe = this.recipeRepository.findById(id).orElseThrow(() -> new RecipeException(
                RecipeException.Type.INVALID_ID, "No such Recipe for id: " + id
        ));
        return this.getFullView(recipe, viewer);
    }

    @Override
    @PreAuthorize("@recipeSecurity.isViewableBy(#username, #slug, #viewer)")
    public FullRecipeView getFullViewByUsernameAndSlug(String username, String slug, @Nullable User viewer) throws RecipeException {
        final RecipeEntity recipe = this.recipeRepository.findByOwnerUsernameAndSlug(username, slug)
                .orElseThrow(() -> new RecipeException(
                        RecipeException.Type.INVALID_USERNAME_OR_SLUG,
                        "No such Recipe for username " + username + " and slug: " + slug
                ));
        return this.getFullView(recipe, viewer);
    }

    @Override
    public Slice<RecipeInfoView> getInfoViewsViewableBy(Pageable pageable, @Nullable User viewer) {
        return this.recipeRepository.findAllViewableBy((UserEntity) viewer, pageable).map(recipe ->
                this.getInfoView(recipe, viewer)
        );
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
    public List<Recipe> getRecipesViewableBy(User viewer) {
        return List.copyOf(this.recipeRepository.findAllByViewersContaining((UserEntity) viewer));
    }

    @Override
    public List<Recipe> getRecipesOwnedBy(User owner) {
        return List.copyOf(this.recipeRepository.findAllByOwner((UserEntity) owner));
    }

    @Override
    @PreAuthorize("@recipeSecurity.isOwner(#id, #modifier)")
    public Recipe update(long id, RecipeUpdateSpec spec, User modifier) throws RecipeException {
        final RecipeEntity entity = this.findRecipeEntity(id);
        boolean didModify = false;
        if (spec.getSlug() != null) {
            entity.setSlug(spec.getSlug());
            didModify = true;
        }
        if (spec.getTitle() != null) {
            entity.setTitle(spec.getTitle());
            didModify = true;
        }
        if (spec.getPreparationTime() != null) {
            entity.setPreparationTime(spec.getPreparationTime());
            didModify = true;
        }
        if (spec.getCookingTime() != null) {
            entity.setCookingTime(spec.getCookingTime());
            didModify = true;
        }
        if (spec.getTotalTime() != null) {
            entity.setTotalTime(spec.getTotalTime());
            didModify = true;
        }
        if (spec.getRawText() != null) {
            entity.setRawText(spec.getRawText());
            didModify = true;
        }
        if (spec.getPublic() != null) {
            entity.setPublic(spec.getPublic());
            didModify = true;
        }
        if (spec.getMainImage() != null) {
            entity.setMainImage((S3ImageEntity) spec.getMainImage());
            didModify = true;
        }
        if (didModify) {
            entity.setModified(LocalDateTime.now());
        }
        return this.recipeRepository.save(entity);
    }

    @Override
    @PreAuthorize("@recipeSecurity.isOwner(#id, #modifier)")
    public Recipe addViewer(long id, User modifier, User viewer) throws RecipeException {
        final RecipeEntity entity = this.recipeRepository.findByIdWithViewers(id).orElseThrow(() -> new RecipeException(
                RecipeException.Type.INVALID_ID, "No such Recipe with id: " + id
        ));
        final Set<UserEntity> viewers = new HashSet<>(entity.getViewerEntities());
        viewers.add((UserEntity) viewer);
        entity.setViewers(viewers);
        return this.recipeRepository.save(entity);
    }

    @Override
    @PreAuthorize("@recipeSecurity.isOwner(#id, #modifier)")
    public Recipe removeViewer(long id, User modifier, User viewer) throws RecipeException {
        final RecipeEntity entity = this.findRecipeEntity(id);
        final Set<UserEntity> viewers = new HashSet<>(entity.getViewerEntities());
        viewers.remove((UserEntity) viewer);
        entity.setViewers(viewers);
        return this.recipeRepository.save(entity);
    }

    @Override
    @PreAuthorize("@recipeSecurity.isOwner(#id, #modifier)")
    public Recipe clearAllViewers(long id, User modifier) throws RecipeException {
        final RecipeEntity entity = this.findRecipeEntity(id);
        entity.setViewers(new HashSet<>());
        return this.recipeRepository.save(entity);
    }

    @Override
    @PreAuthorize("@recipeSecurity.isOwner(#id, #modifier)")
    public void deleteRecipe(long id, User modifier) {
        this.recipeRepository.deleteById(id);
    }

}
