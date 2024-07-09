package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.recipe.view.RecipeExceptionView;
import app.mealsmadeeasy.api.recipe.view.RecipeGetView;
import app.mealsmadeeasy.api.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recipe")
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

    @GetMapping("/{id}")
    public ResponseEntity<RecipeGetView> getById(@PathVariable long id, @AuthenticationPrincipal User user)
            throws RecipeException {
        final Recipe recipe;
        if (user != null) {
            recipe = this.recipeService.getById(id, user);
        } else {
            recipe = this.recipeService.getById(id);
        }
        return ResponseEntity.ok(new RecipeGetView(recipe.getId(), recipe.getTitle()));
    }

}
