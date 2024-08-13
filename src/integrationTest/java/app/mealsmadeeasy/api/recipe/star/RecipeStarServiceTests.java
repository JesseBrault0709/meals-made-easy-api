package app.mealsmadeeasy.api.recipe.star;

import app.mealsmadeeasy.api.recipe.Recipe;
import app.mealsmadeeasy.api.recipe.RecipeService;
import app.mealsmadeeasy.api.recipe.spec.RecipeCreateSpec;
import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserCreateException;
import app.mealsmadeeasy.api.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class RecipeStarServiceTests {

    @Autowired
    private RecipeStarService recipeStarService;

    @Autowired
    private UserService userService;

    @Autowired
    private RecipeService recipeService;

    private User getTestUser(String username) {
        try {
            return this.userService.createUser(username, username + "@test.com", "test");
        } catch (UserCreateException e) {
            throw new RuntimeException(e);
        }
    }

    private Recipe getTestRecipe(User owner, String slug, boolean isPublic) {
        final RecipeCreateSpec spec = new RecipeCreateSpec();
        spec.setSlug(slug);
        spec.setTitle("Test Recipe");
        spec.setRawText("My great recipe has five ingredients.");
        spec.setPublic(isPublic);
        return this.recipeService.create(owner, spec);
    }

    @Test
    @DirtiesContext
    public void createViaUsernameAndSlug() {
        final User owner = this.getTestUser("recipe-owner");
        final User starer = this.getTestUser("recipe-starer");
        final Recipe recipe = this.getTestRecipe(owner, "test-recipe", true);
        final RecipeStar star = assertDoesNotThrow(() -> this.recipeStarService.create(
                recipe.getOwner().getUsername(),
                recipe.getSlug(),
                starer
        ));
        assertThat(star.getDate(), is(notNullValue()));
    }

    @Test
    @DirtiesContext
    public void createViaId() {
        final User owner = this.getTestUser("recipe-owner");
        final User starer = this.getTestUser("recipe-starer");
        final Recipe recipe = this.getTestRecipe(owner, "test-recipe", true);
        final RecipeStar star = assertDoesNotThrow(() -> this.recipeStarService.create(
                recipe.getId(),
                starer.getUsername()
        ));
        assertThat(star.getDate(), is(notNullValue()));
    }

}
