package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.image.ImageService;
import app.mealsmadeeasy.api.image.S3ImageEntity;
import app.mealsmadeeasy.api.recipe.spec.RecipeCreateSpec;
import app.mealsmadeeasy.api.recipe.spec.RecipeUpdateSpec;
import app.mealsmadeeasy.api.recipe.view.FullRecipeView;
import app.mealsmadeeasy.api.recipe.view.RecipeInfoView;
import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserEntity;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
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

    @Override
    @PostAuthorize("@recipeSecurity.isViewableBy(#id, #viewer)")
    public FullRecipeView getFullViewById(long id, @Nullable User viewer) throws RecipeException {
        final RecipeEntity recipe = this.recipeRepository.findById(id).orElseThrow(() -> new RecipeException(
                RecipeException.Type.INVALID_ID, "No such Recipe for id: " + id
        ));
        final FullRecipeView view = new FullRecipeView();
        view.setId(recipe.getId());
        view.setCreated(recipe.getCreated());
        view.setModified(recipe.getModified());
        view.setTitle(recipe.getTitle());
        view.setText(this.getRenderedMarkdown(recipe));
        view.setOwnerId(recipe.getOwner().getId());
        view.setOwnerUsername(recipe.getOwner().getUsername());
        view.setStarCount(this.getStarCount(recipe));
        view.setViewerCount(this.getViewerCount(recipe.getId()));
        view.setMainImage(this.imageService.toImageView(recipe.getMainImage()));
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
            view.setStarCount(this.getStarCount(entity));
            view.setMainImage(this.imageService.toImageView(entity.getMainImage()));
            return view;
        });
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
        if (spec.getTitle() != null) {
            entity.setTitle(spec.getTitle());
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
