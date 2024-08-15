package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.auth.AuthService;
import app.mealsmadeeasy.api.auth.LoginDetails;
import app.mealsmadeeasy.api.auth.LoginException;
import app.mealsmadeeasy.api.recipe.spec.RecipeCreateSpec;
import app.mealsmadeeasy.api.recipe.star.RecipeStarService;
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
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private RecipeStarService recipeStarService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    private User createTestUser(String username) {
        try {
            return this.userService.createUser(username, username + "@test.com", "test");
        } catch (UserCreateException e) {
            throw new RuntimeException(e);
        }
    }

    private Recipe createTestRecipe(User owner, boolean isPublic, String slug) {
        final RecipeCreateSpec spec = new RecipeCreateSpec();
        spec.setSlug(slug);
        spec.setTitle("Test Recipe");
        spec.setPreparationTime(10);
        spec.setCookingTime(20);
        spec.setTotalTime(30);
        spec.setRawText("# Hello, World!");
        spec.setPublic(isPublic);
        return this.recipeService.create(owner, spec);
    }

    private Recipe createTestRecipe(User owner, boolean isPublic) {
        return this.createTestRecipe(owner, isPublic, "test-recipe");
    }

    private String getAccessToken(User user) throws LoginException {
        return this.authService.login(user.getUsername(), "test")
                .getAccessToken()
                .getToken();
    }

    @Test
    @DirtiesContext
    public void getRecipePageViewByIdPublicRecipeNoPrincipal() throws Exception {
        final User owner = this.createTestUser("owner");
        final Recipe recipe = this.createTestRecipe(owner, true);
        this.mockMvc.perform(
                get("/recipes/{username}/{slug}", recipe.getOwner().getUsername(), recipe.getSlug())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipe.id").value(1))
                .andExpect(jsonPath("$.recipe.created").exists()) // TODO: better matching of exact LocalDateTime
                .andExpect(jsonPath("$.recipe.modified").doesNotExist())
                .andExpect(jsonPath("$.recipe.slug").value(recipe.getSlug()))
                .andExpect(jsonPath("$.recipe.title").value("Test Recipe"))
                .andExpect(jsonPath("$.recipe.preparationTime").value(recipe.getPreparationTime()))
                .andExpect(jsonPath("$.recipe.cookingTime").value(recipe.getCookingTime()))
                .andExpect(jsonPath("$.recipe.totalTime").value(recipe.getTotalTime()))
                .andExpect(jsonPath("$.recipe.text").value("<h1>Hello, World!</h1>"))
                .andExpect(jsonPath("$.recipe.rawText").doesNotExist())
                .andExpect(jsonPath("$.recipe.owner.id").value(owner.getId()))
                .andExpect(jsonPath("$.recipe.owner.username").value(owner.getUsername()))
                .andExpect(jsonPath("$.recipe.starCount").value(0))
                .andExpect(jsonPath("$.recipe.viewerCount").value(0))
                .andExpect(jsonPath("$.recipe.isPublic").value(true))
                .andExpect(jsonPath("$.isStarred").value(nullValue()))
                .andExpect(jsonPath("$.isOwner").value(nullValue()));
    }

    @Test
    @DirtiesContext
    public void getFullRecipeViewIncludeRawText() throws Exception {
        final User owner = this.createTestUser("owner");
        final Recipe recipe = this.createTestRecipe(owner, true);
        this.mockMvc.perform(
                get(
                        "/recipes/{username}/{slug}?includeRawText=true",
                        recipe.getOwner().getUsername(),
                        recipe.getSlug()
                )
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipe.rawText").value(recipe.getRawText()));
    }

    @Test
    @DirtiesContext
    public void getFullRecipeViewPrincipalIsStarer() throws Exception {
        final User owner = this.createTestUser("owner");
        final Recipe recipe = this.createTestRecipe(owner, false);
        this.recipeStarService.create(recipe.getId(), owner.getUsername());
        final String accessToken = this.getAccessToken(owner);
        this.mockMvc.perform(
                get("/recipes/{username}/{slug}", recipe.getOwner().getUsername(), recipe.getSlug())
                        .header("Authorization", "Bearer " + accessToken)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isStarred").value(true));
    }

    @Test
    @DirtiesContext
    public void getFullRecipeViewPrincipalIsNotStarer() throws Exception {
        final User owner = this.createTestUser("owner");
        final Recipe recipe = this.createTestRecipe(owner, false);
        final String accessToken = this.getAccessToken(owner);
        this.mockMvc.perform(
                get("/recipes/{username}/{slug}", recipe.getOwner().getUsername(), recipe.getSlug())
                        .header("Authorization", "Bearer " + accessToken)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isStarred").value(false));
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
                .andExpect(jsonPath("$.content[0].created").exists()) // TODO: better matching of exact LocalDateTime
                .andExpect(jsonPath("$.content[0].modified").doesNotExist())
                .andExpect(jsonPath("$.content[0].slug").value(recipe.getSlug()))
                .andExpect(jsonPath("$.content[0].title").value(recipe.getTitle()))
                .andExpect(jsonPath("$.content[0].preparationTime").value(recipe.getPreparationTime()))
                .andExpect(jsonPath("$.content[0].cookingTime").value(recipe.getCookingTime()))
                .andExpect(jsonPath("$.content[0].totalTime").value(recipe.getTotalTime()))
                .andExpect(jsonPath("$.content[0].owner.id").value(owner.getId()))
                .andExpect(jsonPath("$.content[0].owner.username").value(owner.getUsername()))
                .andExpect(jsonPath("$.content[0].isPublic").value(true))
                .andExpect(jsonPath("$.content[0].starCount").value(0));
    }

    @Test
    @DirtiesContext
    public void getRecipeInfoViewsWithPrincipalIncludesPrivate() throws Exception {
        final User owner = this.createTestUser("owner");
        final Recipe r0 = this.createTestRecipe(owner, true, "r0");
        final Recipe r1 = this.createTestRecipe(owner, true, "r1");
        final Recipe r2 = this.createTestRecipe(owner, false, "r2");
        final LoginDetails loginDetails = this.authService.login(owner.getUsername(), "test");
        this.mockMvc.perform(
                get("/recipes")
                        .header("Authorization", "Bearer " + loginDetails.getAccessToken().getToken())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slice.number").value(0))
                .andExpect(jsonPath("$.slice.size").value(20))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(3)));
    }

    @Test
    @DirtiesContext
    public void addStarToRecipe() throws Exception {
        final User owner = this.createTestUser("recipe-owner");
        final User starer = this.createTestUser("recipe-starer");
        final Recipe recipe = this.createTestRecipe(owner, true);
        this.mockMvc.perform(
                post("/recipes/{username}/{slug}/star", recipe.getOwner().getUsername(), recipe.getSlug())
                        .header("Authorization", "Bearer " + this.getAccessToken(starer))
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.date").exists());
    }

    @Test
    @DirtiesContext
    public void getStarForRecipe() throws Exception {
        final User owner = this.createTestUser("recipe-owner");
        final User starer = this.createTestUser("recipe-starer");
        final Recipe recipe = this.createTestRecipe(owner, true);
        this.recipeStarService.create(recipe.getId(), starer.getUsername());
        this.mockMvc.perform(
                get("/recipes/{username}/{slug}/star", recipe.getOwner().getUsername(), recipe.getSlug())
                        .header("Authorization", "Bearer " + this.getAccessToken(starer))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isStarred").value(true))
                .andExpect(jsonPath("$.star").isMap())
                .andExpect(jsonPath("$.star.date").exists());
    }

    @Test
    @DirtiesContext
    public void deleteStarFromRecipe() throws Exception {
        final User owner = this.createTestUser("recipe-owner");
        final User starer = this.createTestUser("recipe-starer");
        final Recipe recipe = this.createTestRecipe(owner, true);
        this.recipeStarService.create(recipe.getId(), starer.getUsername());
        this.mockMvc.perform(
                delete("/recipes/{username}/{slug}/star", recipe.getOwner().getUsername(), recipe.getSlug())
                        .header("Authorization", "Bearer " + this.getAccessToken(starer))
        )
                .andExpect(status().isNoContent());
    }

}
