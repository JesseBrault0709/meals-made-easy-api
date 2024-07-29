package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.recipe.view.FullRecipeView;
import app.mealsmadeeasy.api.recipe.view.RecipeExceptionView;
import app.mealsmadeeasy.api.recipe.view.RecipeInfoView;
import app.mealsmadeeasy.api.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @ExceptionHandler
    public ResponseEntity<RecipeExceptionView> onRecipeException(RecipeException recipeException) {
        return ResponseEntity.badRequest().body(new RecipeExceptionView(
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

}
