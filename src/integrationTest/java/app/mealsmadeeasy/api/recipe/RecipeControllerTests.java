package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserCreateException;
import app.mealsmadeeasy.api.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
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
    @DirtiesContext
    public void getRecipePageViewByIdPublicRecipeNoPrincipal() throws Exception {
        final User owner = this.createTestUser("owner");
        final Recipe recipe = this.createTestRecipe(owner);
        this.recipeService.setPublic(recipe, owner, true);
        this.mockMvc.perform(get("/recipes/{id}", recipe.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Recipe"))
                .andExpect(jsonPath("$.text").value("<h1>Hello, World!</h1>"))
                .andExpect(jsonPath("$.ownerId").value(owner.getId()))
                .andExpect(jsonPath("$.ownerUsername").value(owner.getUsername()))
                .andExpect(jsonPath("$.starCount").value(0))
                .andExpect(jsonPath("$.viewerCount").value(0));
    }

    @Test
    @DirtiesContext
    public void getRecipeInfoViewsNoPrincipal() throws Exception {
        final User owner = this.createTestUser("owner");
        final Recipe recipe = this.createTestRecipe(owner);
        this.recipeService.setPublic(recipe, owner, true);
        this.mockMvc.perform(get("/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slice.number").value(0))
                .andExpect(jsonPath("$.slice.size").value(20))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(recipe.getId()))
                .andExpect(jsonPath("$.content[0].updated").exists())
                .andExpect(jsonPath("$.content[0].title").value(recipe.getTitle()))
                .andExpect(jsonPath("$.content[0].ownerId").value(owner.getId()))
                .andExpect(jsonPath("$.content[0].ownerUsername").value(owner.getUsername()))
                .andExpect(jsonPath("$.content[0].public").value(true))
                .andExpect(jsonPath("$.content[0].starCount").value(0));
    }

}
