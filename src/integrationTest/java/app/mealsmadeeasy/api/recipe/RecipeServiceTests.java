package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.recipe.spec.RecipeCreateSpec;
import app.mealsmadeeasy.api.recipe.spec.RecipeUpdateSpec;
import app.mealsmadeeasy.api.recipe.star.RecipeStar;
import app.mealsmadeeasy.api.recipe.star.RecipeStarService;
import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserEntity;
import app.mealsmadeeasy.api.user.UserRepository;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

// TODO: test mainImage included
@SpringBootTest
public class RecipeServiceTests {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private RecipeStarService recipeStarService;

    @Autowired
    private UserRepository userRepository;

    private UserEntity createTestUser(String username) {
        final UserEntity draft = UserEntity.getDefaultDraft();
        draft.setUsername(username);
        draft.setEmail(username + "@test.com");
        draft.setPassword("test");
        return this.userRepository.save(draft);
    }

    private Recipe createTestRecipe(@Nullable User owner) {
        return this.createTestRecipe(owner, false);
    }

    private Recipe createTestRecipe(@Nullable User owner, boolean isPublic) {
        final RecipeCreateSpec spec = new RecipeCreateSpec();
        spec.setTitle("My Recipe");
        spec.setRawText("Hello!");
        spec.setPublic(isPublic);
        return this.recipeService.create(owner, spec);
    }

    @Test
    public void smokeScreen() {}

    @Test
    @DirtiesContext
    public void create() {
        final User user = this.createTestUser("recipeOwner");
        final Recipe recipe = this.createTestRecipe(user);
        assertThat(recipe.getOwner().getUsername(), is(user.getUsername()));
        assertThat(recipe.getTitle(), is("My Recipe"));
        assertThat(recipe.getRawText(), is("Hello!"));
    }

    @Test
    @DirtiesContext
    public void createWithoutOwnerThrowsAccessDenied() {
        assertThrows(AccessDeniedException.class, () -> this.recipeService.create(null, new RecipeCreateSpec()));
    }

    @Test
    @DirtiesContext
    public void getByIdPublicNoViewerDoesNotThrow() {
        final User owner = this.createTestUser("recipeOwner");
        final Recipe recipe = this.createTestRecipe(owner, true);
        assertDoesNotThrow(() -> this.recipeService.getById(recipe.getId(), null));
    }

    @Test
    @DirtiesContext
    public void getByIdHasCorrectProperties() throws RecipeException {
        final User owner = this.createTestUser("recipeOwner");
        final Recipe recipe = this.createTestRecipe(owner, true);
        final Recipe byId = this.recipeService.getById(recipe.getId(), null);
        assertThat(byId.getId(), is(recipe.getId()));
        assertThat(byId.getTitle(), is("My Recipe"));
        assertThat(byId.getRawText(), is("Hello!"));
        assertThat(byId.isPublic(), is(true));
    }

    @Test
    @DirtiesContext
    public void getByIdThrowsWhenNotPublicAndNoViewer() {
        final User owner = this.createTestUser("recipeOwner");
        final Recipe recipe = this.createTestRecipe(owner, false); // not public
        assertThrows(AccessDeniedException.class, () -> this.recipeService.getById(recipe.getId(), null));
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
    public void getByIdOkayWhenPublicAndNoViewer() {
        final User owner = this.createTestUser("recipeOwner");
        final Recipe recipe = this.createTestRecipe(owner, true);
        assertDoesNotThrow(() -> this.recipeService.getById(recipe.getId(), null));
    }

    @Test
    @DirtiesContext
    public void getByIdOkayWhenPublicRecipeWithViewer() {
        final User owner = this.createTestUser("recipeOwner");
        final User viewer = this.createTestUser("viewer");
        final Recipe recipe = this.createTestRecipe(owner, true);
        assertDoesNotThrow(() -> this.recipeService.getById(recipe.getId(), viewer));
    }

    @Test
    @DirtiesContext
    public void getByIdOkayWithStarsPublicAndNoViewer() {
        final User owner = this.createTestUser("recipeOwner");
        final Recipe recipe = this.createTestRecipe(owner, true);
        final RecipeStar star = this.recipeStarService.create(recipe.getId(), owner.getUsername());
        final Recipe byIdWithStars = assertDoesNotThrow(() -> this.recipeService.getByIdWithStars(
                recipe.getId(), null
        ));
        assertThat(byIdWithStars.getStars(), ContainsRecipeStarsMatcher.containsStars(star));
    }

    @Test
    @DirtiesContext
    public void getByIdOkayWithStarsThrowsWhenNotViewer() {
        final User owner = this.createTestUser("recipeOwner");
        final User notViewer = this.createTestUser("notViewer");
        final Recipe recipe = this.createTestRecipe(owner);
        assertThrows(AccessDeniedException.class, () -> this.recipeService.getByIdWithStars(recipe.getId(), notViewer));
    }

    @Test
    @DirtiesContext
    public void getByIdWithStarsOkayWhenPublicRecipeWithViewer() throws RecipeException {
        final User owner = this.createTestUser("recipeOwner");
        final User viewer = this.createTestUser("viewer");
        final Recipe notYetPublicRecipe = this.createTestRecipe(owner);
        final RecipeUpdateSpec updateSpec = new RecipeUpdateSpec();
        updateSpec.setPublic(true);
        final Recipe publicRecipe = this.recipeService.update(notYetPublicRecipe.getId(), updateSpec, owner);
        assertDoesNotThrow(() -> this.recipeService.getByIdWithStars(publicRecipe.getId(), viewer));
    }

    @Test
    @DirtiesContext
    public void getByMinimumStarsAllPublic() {
        final User owner = this.createTestUser("recipeOwner");
        final User u0 = this.createTestUser("u0");
        final User u1 = this.createTestUser("u1");

        final Recipe r0 = this.createTestRecipe(owner, true);
        final Recipe r1 = this.createTestRecipe(owner, true);
        final Recipe r2 = this.createTestRecipe(owner, true);

        // r0.stars = 0, r1.stars = 1, r2.stars = 2
        this.recipeStarService.create(r1.getId(), u0.getUsername());
        this.recipeStarService.create(r2.getId(), u0.getUsername());
        this.recipeStarService.create(r2.getId(), u1.getUsername());

        final List<Recipe> zeroStars = this.recipeService.getByMinimumStars(0, null);
        final List<Recipe> oneStar = this.recipeService.getByMinimumStars(1, null);
        final List<Recipe> twoStars = this.recipeService.getByMinimumStars(2, null);

        assertThat(zeroStars.size(), is(3));
        assertThat(oneStar.size(), is(2));
        assertThat(twoStars.size(), is(1));

        assertThat(zeroStars, ContainsRecipesMatcher.containsRecipes(r0, r1, r2));
        assertThat(oneStar, ContainsRecipesMatcher.containsRecipes(r1, r2));
        assertThat(twoStars, ContainsRecipesMatcher.containsRecipes(r2));
    }

    @Test
    @DirtiesContext
    public void getByMinimumStarsOnlySomeViewable() throws RecipeException {
        final User owner = this.createTestUser("recipeOwner");
        final User u0 = this.createTestUser("u0");
        final User u1 = this.createTestUser("u1");
        final User viewer = this.createTestUser("recipeViewer");

        Recipe r0 = this.createTestRecipe(owner); // not public
        Recipe r1 = this.createTestRecipe(owner);
        Recipe r2 = this.createTestRecipe(owner);

        for (final User starer : List.of(u0, u1)) {
            r0 = this.recipeService.addViewer(r0.getId(), owner, starer);
            r1 = this.recipeService.addViewer(r1.getId(), owner, starer);
            r2 = this.recipeService.addViewer(r2.getId(), owner, starer);
        }

        // r0.stars = 0, r1.stars = 1, r2.stars = 2
        this.recipeStarService.create(r1.getId(), u0.getUsername());
        this.recipeStarService.create(r2.getId(), u0.getUsername());
        this.recipeStarService.create(r2.getId(), u1.getUsername());

        final List<Recipe> zeroStarsNoneViewable = this.recipeService.getByMinimumStars(0, viewer);
        final List<Recipe> oneStarNoneViewable = this.recipeService.getByMinimumStars(1, viewer);
        final List<Recipe> twoStarsNoneViewable = this.recipeService.getByMinimumStars(2, viewer);

        assertThat(zeroStarsNoneViewable.size(), is(0));
        assertThat(oneStarNoneViewable.size(), is(0));
        assertThat(twoStarsNoneViewable.size(), is(0));

        // Now make them viewable
        r0 = this.recipeService.addViewer(r0.getId(), owner, viewer);
        r1 = this.recipeService.addViewer(r1.getId(), owner, viewer);
        r2 = this.recipeService.addViewer(r2.getId(), owner, viewer);

        final List<Recipe> zeroStarsViewable = this.recipeService.getByMinimumStars(0, viewer);
        final List<Recipe> oneStarViewable = this.recipeService.getByMinimumStars(1, viewer);
        final List<Recipe> twoStarsViewable = this.recipeService.getByMinimumStars(2, viewer);

        assertThat(zeroStarsViewable.size(), is(3));
        assertThat(oneStarViewable.size(), is(2));
        assertThat(twoStarsViewable.size(), is (1));

        assertThat(zeroStarsViewable, ContainsRecipesMatcher.containsRecipes(r0, r1, r2));
        assertThat(oneStarViewable, ContainsRecipesMatcher.containsRecipes(r1, r2));
        assertThat(twoStarsViewable, ContainsRecipesMatcher.containsRecipes(r2));
    }

    @Test
    @DirtiesContext
    public void getPublicRecipes() {
        final User owner = this.createTestUser("recipeOwner");

        Recipe r0 = this.createTestRecipe(owner, true);
        Recipe r1 = this.createTestRecipe(owner, true);

        final List<Recipe> publicRecipes = this.recipeService.getPublicRecipes();
        assertThat(publicRecipes.size(), is(2));
        assertThat(publicRecipes, ContainsRecipesMatcher.containsRecipes(r0, r1));
    }

    @Test
    @DirtiesContext
    public void getRecipesViewableByUser() throws RecipeException {
        final User owner = this.createTestUser("recipeOwner");
        final User viewer = this.createTestUser("recipeViewer");

        Recipe r0 = this.createTestRecipe(owner);
        r0 = this.recipeService.addViewer(r0.getId(), owner, viewer);
        final List<Recipe> viewableRecipes = this.recipeService.getRecipesViewableBy(viewer);
        assertThat(viewableRecipes.size(), is(1));
        assertThat(viewableRecipes, ContainsRecipesMatcher.containsRecipes(r0));
    }

    @Test
    @DirtiesContext
    public void getRecipesOwnedByUser() {
        final User owner = this.createTestUser("recipeOwner");
        final Recipe r0 = this.createTestRecipe(owner);
        final List<Recipe> ownedRecipes = this.recipeService.getRecipesOwnedBy(owner);
        assertThat(ownedRecipes.size(), is(1));
        assertThat(ownedRecipes, ContainsRecipesMatcher.containsRecipes(r0));
    }

    @Test
    @DirtiesContext
    public void updateRawText() throws RecipeException {
        final User owner = this.createTestUser("recipeOwner");
        final RecipeCreateSpec createSpec = new RecipeCreateSpec();
        createSpec.setTitle("My Recipe");
        createSpec.setRawText("# A Heading");
        Recipe recipe = this.recipeService.create(owner, createSpec);
        final String newRawText = "# A Heading\n## A Subheading";
        final RecipeUpdateSpec updateSpec = new RecipeUpdateSpec();
        updateSpec.setRawText(newRawText);
        recipe = this.recipeService.update(recipe.getId(), updateSpec, owner);
        assertThat(recipe.getRawText(), is(newRawText));
    }

    @Test
    @DirtiesContext
    public void updateRawTextThrowsIfNotOwner() {
        final User owner = this.createTestUser("recipeOwner");
        final User notOwner = this.createTestUser("notOwner");
        final Recipe recipe = this.createTestRecipe(owner);
        final RecipeUpdateSpec updateSpec = new RecipeUpdateSpec();
        updateSpec.setRawText("should fail");
        assertThrows(
                AccessDeniedException.class,
                () -> this.recipeService.update(recipe.getId(), updateSpec, notOwner)
        );
    }

    @Test
    @DirtiesContext
    public void deleteRecipe() {
        final User owner = this.createTestUser("recipeOwner");
        final Recipe toDelete = this.createTestRecipe(owner);
        this.recipeService.deleteRecipe(toDelete.getId(), owner);
        assertThrows(RecipeException.class, () -> this.recipeService.getById(toDelete.getId(), owner));
    }

    @Test
    @DirtiesContext
    public void deleteRecipeThrowsIfNotOwner() {
        final User owner = this.createTestUser("recipeOwner");
        final User notOwner = this.createTestUser("notOwner");
        final Recipe toDelete = this.createTestRecipe(owner);
        assertThrows(AccessDeniedException.class, () -> this.recipeService.deleteRecipe(toDelete.getId(), notOwner));
    }

}
