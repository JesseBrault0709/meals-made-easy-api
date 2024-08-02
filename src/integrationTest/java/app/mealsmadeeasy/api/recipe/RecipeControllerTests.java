package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.recipe.spec.RecipeCreateSpec;
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

    private Recipe createTestRecipe(User owner, boolean isPublic) {
        final RecipeCreateSpec spec = new RecipeCreateSpec();
        spec.setSlug("test-recipe");
        spec.setTitle("Test Recipe");
        spec.setRawText("# Hello, World!");
        spec.setPublic(isPublic);
        return this.recipeService.create(owner, spec);
    }

    @Test
    @DirtiesContext
    public void getRecipePageViewByIdPublicRecipeNoPrincipal() throws Exception {
        final User owner = this.createTestUser("owner");
        final Recipe recipe = this.createTestRecipe(owner, true);
        this.mockMvc.perform(get("/recipes/{username}/{slug}", recipe.getOwner().getUsername(), recipe.getSlug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.slug").value(recipe.getSlug()))
                .andExpect(jsonPath("$.title").value("Test Recipe"))
                .andExpect(jsonPath("$.text").value("<h1>Hello, World!</h1>"))
                .andExpect(jsonPath("$.ownerUsername").value(owner.getUsername()))
                .andExpect(jsonPath("$.starCount").value(0))
                .andExpect(jsonPath("$.viewerCount").value(0))
                .andExpect(jsonPath("$.isPublic").value(true));
    }

    @Test
    @DirtiesContext
    public void getRecipeInfoViewsNoPrincipal() throws Exception {
        final User owner = this.createTestUser("owner");
        final Recipe recipe = this.createTestRecipe(owner, true);
        this.mockMvc.perform(get("/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slice.number").value(0))
                .andExpect(jsonPath("$.slice.size").value(20))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(recipe.getId()))
                .andExpect(jsonPath("$.content[0].updated").exists())
                .andExpect(jsonPath("$.content[0].slug").value(recipe.getSlug()))
                .andExpect(jsonPath("$.content[0].title").value(recipe.getTitle()))
                .andExpect(jsonPath("$.content[0].ownerUsername").value(owner.getUsername()))
                .andExpect(jsonPath("$.content[0].isPublic").value(true))
                .andExpect(jsonPath("$.content[0].starCount").value(0));
    }

}
