package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.recipe.star.RecipeStar;
import app.mealsmadeeasy.api.recipe.star.RecipeStarService;
import app.mealsmadeeasy.api.recipe.view.FullRecipeView;
import app.mealsmadeeasy.api.recipe.view.RecipeExceptionView;
import app.mealsmadeeasy.api.recipe.view.RecipeInfoView;
import app.mealsmadeeasy.api.user.User;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeStarService recipeStarService;

    public RecipeController(RecipeService recipeService, RecipeStarService recipeStarService) {
        this.recipeService = recipeService;
        this.recipeStarService = recipeStarService;
    }

    @ExceptionHandler(RecipeException.class)
    public ResponseEntity<RecipeExceptionView> onRecipeException(RecipeException recipeException) {
        final HttpStatus status = switch (recipeException.getType()) {
            case INVALID_ID, INVALID_USERNAME_OR_SLUG -> HttpStatus.NOT_FOUND;
            case INVALID_COMMENT_ID -> HttpStatus.BAD_REQUEST;
        };
        return ResponseEntity.status(status.value()).body(new RecipeExceptionView(
                recipeException.getType().toString(),
                recipeException.getMessage()
        ));
    }

    @GetMapping("/{username}/{slug}")
    public ResponseEntity<FullRecipeView> getById(
            @PathVariable String username,
            @PathVariable String slug,
            @AuthenticationPrincipal User viewer
    )
            throws RecipeException {
        return ResponseEntity.ok(this.recipeService.getFullViewByUsernameAndSlug(username, slug, viewer));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getRecipeInfoViews(
            Pageable pageable,
            @AuthenticationPrincipal User user
    ) {
        final Slice<RecipeInfoView> slice = this.recipeService.getInfoViewsViewableBy(pageable, user);
        final Map<String, Object> view = new HashMap<>();
        view.put("content", slice.getContent());
        final Map<String, Object> sliceInfo = new HashMap<>();
        sliceInfo.put("size", slice.getSize());
        sliceInfo.put("number", slice.getNumber());
        view.put("slice", sliceInfo);
        return ResponseEntity.ok(view);
    }

    @PostMapping("/{username}/{slug}/star")
    public ResponseEntity<RecipeStar> addStar(
            @PathVariable String username,
            @PathVariable String slug,
            @AuthenticationPrincipal User principal
    ) throws RecipeException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.recipeStarService.create(username, slug, principal));
    }

    @GetMapping("/{username}/{slug}/star")
    public ResponseEntity<Map<String, Object>> getStar(
            @PathVariable String username,
            @PathVariable String slug,
            @AuthenticationPrincipal User principal
    ) throws RecipeException {
        final @Nullable RecipeStar star = this.recipeStarService.find(username, slug, principal).orElse(null);
        if (star != null) {
            return ResponseEntity.ok(Map.of("isStarred", true, "star", star));
        } else {
            return ResponseEntity.ok(Map.of("isStarred", false));
        }
    }

    @DeleteMapping("/{username}/{slug}/star")
    public ResponseEntity<Object> removeStar(
            @PathVariable String username,
            @PathVariable String slug,
            @AuthenticationPrincipal User principal
    ) throws RecipeException {
        this.recipeStarService.delete(username, slug, principal);
        return ResponseEntity.noContent().build();
    }

}
