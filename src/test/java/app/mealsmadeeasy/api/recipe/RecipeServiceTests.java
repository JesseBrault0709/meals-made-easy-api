package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserEntity;
import app.mealsmadeeasy.api.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

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
        return this.recipeService.create(owner.getUsername(), "My Recipe" , "Hello!");
    }

    @Test
    @DirtiesContext
    public void simpleCreate() throws RecipeException {
        final User user = this.createTestUser("recipeOwner");
        final Recipe recipe = this.recipeService.create(user.getUsername(), "My Recipe" , "Hello!");
        assertThat(recipe.getOwner(), is(user));
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

        final Recipe r0 = this.createTestRecipe(owner);
        final Recipe r1 = this.createTestRecipe(owner);
        final Recipe r2 = this.createTestRecipe(owner);

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
    }

}
