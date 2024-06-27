package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserEntity;
import app.mealsmadeeasy.api.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

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

}
