package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.recipe.star.RecipeStar;
import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserEntity;
import app.mealsmadeeasy.api.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static app.mealsmadeeasy.api.matchers.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

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

    private Recipe createTestRecipe(User owner) {
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
    public void createViaUser() {
        final User user = this.createTestUser("recipeOwner");
        final Recipe recipe = this.recipeService.create(user, "My Recipe", "Hello!");
        assertThat(recipe.getOwner().getUsername(), is(user.getUsername()));
        assertThat(recipe.getTitle(), is("My Recipe"));
        assertThat(recipe.getRawText(), is("Hello!"));
    }

    @Test
    @DirtiesContext
    public void getByIdPublic() throws RecipeException {
        final User owner = this.createTestUser("recipeOwner");
        Recipe recipe = this.createTestRecipe(owner);
        recipe = this.recipeService.setPublic(recipe, true);
        final Recipe byId = this.recipeService.getById(recipe.getId());
        assertThat(byId.getId(), is(recipe.getId()));
        assertThat(byId.getTitle(), is("My Recipe"));
        assertThat(byId.getRawText(), is("Hello!"));
    }

    @Test
    @DirtiesContext
    public void getByIdThrowsWhenNotPublic() {
        final User owner = this.createTestUser("recipeOwner");
        final Recipe recipe = this.createTestRecipe(owner);
        assertThrows(AccessDeniedException.class, () -> this.recipeService.getById(recipe.getId()));
    }

    @Test
    @DirtiesContext
    public void getByIdThrowsWhenNotViewer() {
        final User owner = this.createTestUser("recipeOwner");
        final User notViewer = this.createTestUser("notViewer");
        final Recipe recipe = this.createTestRecipe(owner);
        assertThrows(AccessDeniedException.class, () -> this.recipeService.getById(recipe.getId(), notViewer));
    }

    @Test
    @DirtiesContext
    public void getByIdOkayWhenPublic() {
        final User owner = this.createTestUser("recipeOwner");
        final Recipe notYetPublicRecipe = this.createTestRecipe(owner);
        final Recipe publicRecipe = this.recipeService.setPublic(notYetPublicRecipe, true);
        assertDoesNotThrow(() -> this.recipeService.getById(publicRecipe.getId()));
    }

    @Test
    @DirtiesContext
    public void getByIdOkayWhenPublicRecipeWithViewer() {
        final User owner = this.createTestUser("recipeOwner");
        final User viewer = this.createTestUser("viewer");
        final Recipe notYetPublicRecipe = this.createTestRecipe(owner);
        final Recipe publicRecipe = this.recipeService.setPublic(notYetPublicRecipe, true);
        assertDoesNotThrow(() -> this.recipeService.getById(publicRecipe.getId(), viewer));
    }

    @Test
    @DirtiesContext
    public void getByIdWithStarsPublic() throws RecipeException {
        final User owner = this.createTestUser("recipeOwner");
        Recipe recipe = this.createTestRecipe(owner);
        recipe = this.recipeService.setPublic(recipe, true);
        final RecipeStar star = this.recipeService.addStar(recipe, owner);
        final Recipe byIdWithStars = this.recipeService.getByIdWithStars(recipe.getId());
        assertThat(byIdWithStars.getStars(), containsStars(star));
    }

    @Test
    @DirtiesContext
    public void getByIdWithStarsThrowsWhenNotViewer() {
        final User owner = this.createTestUser("recipeOwner");
        final User notViewer = this.createTestUser("notViewer");
        final Recipe recipe = this.createTestRecipe(owner);
        assertThrows(AccessDeniedException.class, () -> this.recipeService.getByIdWithStars(recipe.getId(), notViewer));
    }

    @Test
    @DirtiesContext
    public void getByIdWithStarsOkayWhenPublic() {
        final User owner = this.createTestUser("recipeOwner");
        final Recipe notYetPublicRecipe = this.createTestRecipe(owner);
        final Recipe publicRecipe = this.recipeService.setPublic(notYetPublicRecipe, true);
        assertDoesNotThrow(() -> this.recipeService.getByIdWithStars(publicRecipe.getId()));
    }

    @Test
    @DirtiesContext
    public void getByIdWithStarsOkayWhenPublicRecipeWithViewer() {
        final User owner = this.createTestUser("recipeOwner");
        final User viewer = this.createTestUser("viewer");
        final Recipe notYetPublicRecipe = this.createTestRecipe(owner);
        final Recipe publicRecipe = this.recipeService.setPublic(notYetPublicRecipe, true);
        assertDoesNotThrow(() -> this.recipeService.getByIdWithStars(publicRecipe.getId(), viewer));
    }

    @Test
    @DirtiesContext
    public void getByMinimumStarsAllPublic() throws RecipeException {
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

        assertThat(zeroStars, containsRecipes(r0, r1, r2));
        assertThat(oneStar, containsRecipes(r1, r2));
        assertThat(twoStars, containsRecipes(r2));
    }

    @Test
    @DirtiesContext
    public void getByMinimumStarsOnlySomeViewable() throws RecipeException {
        final User owner = this.createTestUser("recipeOwner");
        final User u0 = this.createTestUser("u0");
        final User u1 = this.createTestUser("u1");
        final User viewer = this.createTestUser("recipeViewer");

        Recipe r0 = this.createTestRecipe(owner);
        Recipe r1 = this.createTestRecipe(owner);
        Recipe r2 = this.createTestRecipe(owner);

        for (final User starer : List.of(u0, u1)) {
            r0 = this.recipeService.addViewer(r0, starer);
            r1 = this.recipeService.addViewer(r1, starer);
            r2 = this.recipeService.addViewer(r2, starer);
        }

        // r0.stars = 0, r1.stars = 1, r2.stars = 2
        this.recipeService.addStar(r1, u0);
        this.recipeService.addStar(r2, u0);
        this.recipeService.addStar(r2, u1);

        final List<Recipe> zeroStarsNoneViewable = this.recipeService.getByMinimumStars(0, viewer);
        final List<Recipe> oneStarNoneViewable = this.recipeService.getByMinimumStars(1, viewer);
        final List<Recipe> twoStarsNoneViewable = this.recipeService.getByMinimumStars(2, viewer);

        assertThat(zeroStarsNoneViewable.size(), is(0));
        assertThat(oneStarNoneViewable.size(), is(0));
        assertThat(twoStarsNoneViewable.size(), is(0));

        // Now make them viewable
        r0 = this.recipeService.addViewer(r0, viewer);
        r1 = this.recipeService.addViewer(r1, viewer);
        r2 = this.recipeService.addViewer(r2, viewer);

        final List<Recipe> zeroStarsViewable = this.recipeService.getByMinimumStars(0, viewer);
        final List<Recipe> oneStarViewable = this.recipeService.getByMinimumStars(1, viewer);
        final List<Recipe> twoStarsViewable = this.recipeService.getByMinimumStars(2, viewer);

        assertThat(zeroStarsViewable.size(), is(3));
        assertThat(oneStarViewable.size(), is(2));
        assertThat(twoStarsViewable.size(), is (1));

        assertThat(zeroStarsViewable, containsRecipes(r0, r1, r2));
        assertThat(oneStarViewable, containsRecipes(r1, r2));
        assertThat(twoStarsViewable, containsRecipes(r2));
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
        assertThat(publicRecipes, containsRecipes(r0, r1));
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
        assertThat(viewableRecipes, containsRecipes(r0));
    }

    @Test
    @DirtiesContext
    public void getRecipesOwnedByUser() {
        final User owner = this.createTestUser("recipeOwner");
        final Recipe r0 = this.createTestRecipe(owner);
        final List<Recipe> ownedRecipes = this.recipeService.getRecipesOwnedBy(owner);
        assertThat(ownedRecipes.size(), is(1));
        assertThat(ownedRecipes, containsRecipes(r0));
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
    public void updateOwnerViaUser() throws RecipeException {
        final User firstOwner = this.createTestUser("firstOwner");
        final User secondOwner = this.createTestUser("secondOwner");
        Recipe recipe = this.createTestRecipe(firstOwner);
        recipe = this.recipeService.updateOwner(recipe, firstOwner, secondOwner);
        assertThat(recipe.getOwner(), isUser(secondOwner));
    }

    @Test
    @DirtiesContext
    public void updateOwnerViaUserThrowsIfNotOwner() {
        final User actualOwner = this.createTestUser("u0");
        final User notOwner = this.createTestUser("u1");
        final User target = this.createTestUser("u2");
        final Recipe recipe = this.createTestRecipe(actualOwner);
        assertThrows(AccessDeniedException.class, () -> this.recipeService.updateOwner(recipe, notOwner, target));
    }

    @Test
    @DirtiesContext
    public void addStar() throws RecipeException {
        final User owner = this.createTestUser("recipeOwner");
        final User starer = this.createTestUser("starer");
        Recipe recipe = this.createTestRecipe(owner);
        recipe = this.recipeService.addViewer(recipe, starer);
        final RecipeStar star = this.recipeService.addStar(recipe, starer);
        assertThat(star.getRecipe(), isRecipe(recipe));
        assertThat(star.getOwner(), isUser(starer));
    }

    @Test
    @DirtiesContext
    public void addStarWhenNotViewableThrows() {
        final User notViewer = this.createTestUser("notViewer");
        final Recipe recipe = this.createTestRecipe(this.createTestUser("recipeOwner"));
        assertThrows(AccessDeniedException.class, () -> this.recipeService.addStar(recipe, notViewer));
    }

    @Test
    @DirtiesContext
    public void deleteStar() throws RecipeException {
        final User owner = this.createTestUser("recipeOwner");
        final User starer = this.createTestUser("starer");
        Recipe recipe = this.createTestRecipe(owner);
        recipe = this.recipeService.addViewer(recipe, starer);
        final RecipeStar star = this.recipeService.addStar(recipe, starer);
        this.recipeService.deleteStar(star);
        recipe = this.recipeService.getByIdWithStars(recipe.getId(), owner);
        assertThat(recipe.getStars(), is(empty()));
    }

}
