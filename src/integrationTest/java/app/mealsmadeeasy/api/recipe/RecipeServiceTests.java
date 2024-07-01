package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.matchers.Matchers;
import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserEntity;
import app.mealsmadeeasy.api.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static app.mealsmadeeasy.api.matchers.Matchers.isUser;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
public class RecipeServiceTests {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private UserRepository userRepository;

    private UserEntity createTestUser(String username) {
        final UserEntity draft = UserEntity.getDefaultDraft();
        draft.setUsername(username);
        draft.setEmail(username + "@test.com");
        draft.setPassword("test");
        return this.userRepository.save(draft);
    }

    private Recipe createTestRecipe(User owner) throws RecipeException {
        return this.recipeService.create(owner, "My Recipe" , "Hello!");
    }

    @Test
    @DirtiesContext
    public void createViaUsername() throws RecipeException {
        final User user = this.createTestUser("recipeOwner");
        final Recipe recipe = this.recipeService.create(user.getUsername(), "My Recipe" , "Hello!");
        assertThat(recipe.getOwner(), is(user));
        assertThat(recipe.getTitle(), is("My Recipe"));
        assertThat(recipe.getRawText(), is("Hello!"));
    }

    @Test
    @DirtiesContext
    public void createViaUser() throws RecipeException {
        final User user = this.createTestUser("recipeOwner");
        final Recipe recipe = this.recipeService.create(user, "My Recipe", "Hello!");
        assertThat(recipe.getOwner().getUsername(), is(user.getUsername()));
        assertThat(recipe.getTitle(), is("My Recipe"));
        assertThat(recipe.getRawText(), is("Hello!"));
    }

    @Test
    @DirtiesContext
    public void simpleGetById() throws RecipeException {
        final Recipe testRecipe = this.createTestRecipe(this.createTestUser("recipeOwner"));
        final Recipe byId = this.recipeService.getById(testRecipe.getId());
        assertThat(byId.getId(), is(testRecipe.getId()));
        assertThat(byId.getTitle(), is("My Recipe"));
        assertThat(byId.getRawText(), is("Hello!"));
    }

    @Test
    @DirtiesContext
    public void getByMinimumStars() throws RecipeException {
        final User owner = this.createTestUser("recipeOwner");
        final User u0 = this.createTestUser("u0");
        final User u1 = this.createTestUser("u1");

        Recipe r0 = this.createTestRecipe(owner);
        Recipe r1 = this.createTestRecipe(owner);
        Recipe r2 = this.createTestRecipe(owner);

        r0 = this.recipeService.setPublic(r0, true);
        r1 = this.recipeService.setPublic(r1, true);
        r2 = this.recipeService.setPublic(r2, true);

        // r0.stars = 0, r1.stars = 1, r2.stars = 2
        this.recipeService.addStar(r1, u0);
        this.recipeService.addStar(r2, u0);
        this.recipeService.addStar(r2, u1);

        final List<Recipe> zeroStars = this.recipeService.getByMinimumStars(0);
        final List<Recipe> oneStar = this.recipeService.getByMinimumStars(1);
        final List<Recipe> twoStars = this.recipeService.getByMinimumStars(2);

        assertThat(zeroStars.size(), is(3));
        assertThat(oneStar.size(), is(2));
        assertThat(twoStars.size(), is(1));

        assertThat(zeroStars, Matchers.containsRecipes(r0, r1, r2));
        assertThat(oneStar, Matchers.containsRecipes(r1, r2));
        assertThat(twoStars, Matchers.containsRecipes(r2));
    }

    @Test
    @DirtiesContext
    public void getPublicRecipes() throws RecipeException {
        final User owner = this.createTestUser("recipeOwner");

        Recipe r0 = this.createTestRecipe(owner);
        Recipe r1 = this.createTestRecipe(owner);

        r0 = this.recipeService.setPublic(r0, true);
        r1 = this.recipeService.setPublic(r1, true);

        final List<Recipe> publicRecipes = this.recipeService.getPublicRecipes();
        assertThat(publicRecipes.size(), is(2));
        assertThat(publicRecipes, Matchers.containsRecipes(r0, r1));
    }

    @Test
    @DirtiesContext
    public void getRecipesViewableByUser() throws RecipeException {
        final User owner = this.createTestUser("recipeOwner");
        final User viewer = this.createTestUser("recipeViewer");

        Recipe r0 = this.createTestRecipe(owner);
        r0 = this.recipeService.addViewer(r0, viewer);
        final List<Recipe> viewableRecipes = this.recipeService.getRecipesViewableBy(viewer);
        assertThat(viewableRecipes.size(), is(1));
        assertThat(viewableRecipes, Matchers.containsRecipes(r0));
    }

    @Test
    @DirtiesContext
    public void getRecipesOwnedByUser() throws RecipeException {
        final User owner = this.createTestUser("recipeOwner");
        final Recipe r0 = this.createTestRecipe(owner);
        final List<Recipe> ownedRecipes = this.recipeService.getRecipesOwnedBy(owner);
        assertThat(ownedRecipes.size(), is(1));
        assertThat(ownedRecipes, Matchers.containsRecipes(r0));
    }

    @Test
    @DirtiesContext
    public void getRenderedMarkdown() {
        final User owner = this.createTestUser("recipeOwner");
        final Recipe recipe = this.recipeService.create(
                owner, "My Recipe", "# A Heading"
        );
        final String rendered = this.recipeService.getRenderedMarkdown(recipe);
        assertThat(rendered, is("<h1>A Heading</h1>"));
    }

    @Test
    @DirtiesContext
    public void updateRawText() {
        final User owner = this.createTestUser("recipeOwner");
        Recipe recipe = this.recipeService.create(
                owner, "My Recipe", "# A Heading"
        );
        final String newRawText = "# A Heading\n## A Subheading";
        recipe = this.recipeService.updateRawText(recipe, newRawText);
        assertThat(recipe.getRawText(), is(newRawText));
    }

    @Test
    @DirtiesContext
    public void updateOwnerViaUsername() throws RecipeException {
        final User firstOwner = this.createTestUser("firstOwner");
        final User secondOwner = this.createTestUser("secondOwner");
        Recipe recipe = this.createTestRecipe(firstOwner);
        recipe = this.recipeService.updateOwner(recipe, secondOwner.getUsername());
        assertThat(recipe.getOwner(), isUser(secondOwner));
    }

    @Test
    @DirtiesContext
    public void updateOwnerViaUser() throws RecipeException {
        final User firstOwner = this.createTestUser("firstOwner");
        final User secondOwner = this.createTestUser("secondOwner");
        Recipe recipe = this.createTestRecipe(firstOwner);
        recipe = this.recipeService.updateOwner(recipe, secondOwner);
        assertThat(recipe.getOwner(), isUser(secondOwner));
    }

}
