package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserCreateException;
import app.mealsmadeeasy.api.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RecipeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private UserService userService;

    private User createTestUser(String username) {
        try {
            return this.userService.createUser(username, username + "@test.com", "test");
        } catch (UserCreateException e) {
            throw new RuntimeException(e);
        }
    }

    private Recipe createTestRecipe(User owner) {
        return this.recipeService.create(owner, "Test Recipe", "# Hello, World!");
    }

    @Test
    public void getByIdPublicRecipeNoPrincipal() throws Exception {
        final User owner = this.createTestUser("owner");
        final Recipe recipe = this.createTestRecipe(owner);
        this.recipeService.setPublic(recipe, owner, true);
        this.mockMvc.perform(get("/recipe/{id}", recipe.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Recipe"))
                .andExpect(jsonPath("$.text").value("<h1>Hello, World!</h1>"))
                .andExpect(jsonPath("$.ownerId").value(owner.getId()))
                .andExpect(jsonPath("$.ownerUsername").value(owner.getUsername()))
                .andExpect(jsonPath("$.starCount").value(0))
                .andExpect(jsonPath("$.viewerCount").value(0));
    }

}
