package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.auth.AuthService;
import app.mealsmadeeasy.api.auth.LoginDetails;
import app.mealsmadeeasy.api.auth.LoginException;
import app.mealsmadeeasy.api.image.Image;
import app.mealsmadeeasy.api.image.ImageService;
import app.mealsmadeeasy.api.image.S3ImageServiceTests;
import app.mealsmadeeasy.api.image.spec.ImageCreateInfoSpec;
import app.mealsmadeeasy.api.recipe.spec.RecipeCreateSpec;
import app.mealsmadeeasy.api.recipe.spec.RecipeUpdateSpec;
import app.mealsmadeeasy.api.recipe.star.RecipeStarService;
import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserCreateException;
import app.mealsmadeeasy.api.user.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.InputStream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class RecipeControllerTests {

    @Container
    private static final MinIOContainer container = new MinIOContainer(
            DockerImageName.parse("minio/minio:latest")
    );

    @DynamicPropertySource
    public static void minioProperties(DynamicPropertyRegistry registry) {
        registry.add("app.mealsmadeeasy.api.minio.endpoint", container::getS3URL);
        registry.add("app.mealsmadeeasy.api.minio.accessKey", container::getUserName);
        registry.add("app.mealsmadeeasy.api.minio.secretKey", container::getPassword);
    }

    private static InputStream getHal9000() {
        return S3ImageServiceTests.class.getClassLoader().getResourceAsStream("HAL9000.svg");
    }

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

    @Autowired
    private ImageService imageService;

    @Autowired
    private ObjectMapper objectMapper;

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

    private Image createHal9000(User owner) {
        try (final InputStream hal9000 = getHal9000()) {
            return this.imageService.create(
                    owner,
                    "HAL9000.svg",
                    hal9000,
                    27881L,
                    new ImageCreateInfoSpec()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
                .andExpect(jsonPath("$.recipe.mainImage").value(nullValue()))
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
                .andExpect(jsonPath("$.content[0].starCount").value(0))
                .andExpect(jsonPath("$.content[0].mainImage").value(nullValue()));
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

    private String getUpdateBody() throws JsonProcessingException {
        final RecipeUpdateSpec spec = new RecipeUpdateSpec();
        spec.setTitle("Updated Test Recipe");
        spec.setPreparationTime(15);
        spec.setCookingTime(30);
        spec.setTotalTime(45);
        spec.setRawText("# Hello, Updated World!");
        spec.setIsPublic(true);
        return this.objectMapper.writeValueAsString(spec);
    }

    @Test
    @DirtiesContext
    public void updateRecipe() throws Exception {
        final User owner = this.createTestUser("owner");
        final Recipe recipe = this.createTestRecipe(owner, false);
        final String accessToken = this.getAccessToken(owner);
        final String body = this.getUpdateBody();
        this.mockMvc.perform(
                post("/recipes/{username}/{slug}", owner.getUsername(), recipe.getSlug())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipe.id").value(recipe.getId()))
                .andExpect(jsonPath("$.recipe.title").value("Updated Test Recipe"))
                .andExpect(jsonPath("$.recipe.preparationTime").value(15))
                .andExpect(jsonPath("$.recipe.cookingTime").value(30))
                .andExpect(jsonPath("$.recipe.totalTime").value(45))
                .andExpect(jsonPath("$.recipe.text").value("<h1>Hello, Updated World!</h1>"))
                .andExpect(jsonPath("$.recipe.rawText").value("# Hello, Updated World!"))
                .andExpect(jsonPath("$.recipe.owner.id").value(owner.getId()))
                .andExpect(jsonPath("$.recipe.owner.username").value(owner.getUsername()))
                .andExpect(jsonPath("$.recipe.starCount").value(0))
                .andExpect(jsonPath("$.recipe.viewerCount").value(0))
                .andExpect(jsonPath("$.recipe.isPublic").value(true))
                .andExpect(jsonPath("$.recipe.mainImage").value(nullValue()))
                .andExpect(jsonPath("$.isStarred").value(false))
                .andExpect(jsonPath("$.isOwner").value(true));
    }

    @Test
    @DirtiesContext
    public void updateRecipeReturnsViewWithMainImage() throws Exception {
        final User owner = this.createTestUser("owner");

        final Image hal9000 = this.createHal9000(owner);

        final RecipeCreateSpec createSpec = new RecipeCreateSpec();
        createSpec.setTitle("Test Recipe");
        createSpec.setSlug("test-recipe");
        createSpec.setPublic(false);
        createSpec.setRawText("# Hello, World!");
        createSpec.setMainImage(hal9000);
        Recipe recipe = this.recipeService.create(owner, createSpec);

        final RecipeUpdateSpec updateSpec = new RecipeUpdateSpec();
        updateSpec.setTitle("Updated Test Recipe");
        updateSpec.setRawText("# Hello, Updated World!");
        final RecipeUpdateSpec.MainImageUpdateSpec mainImageUpdateSpec = new RecipeUpdateSpec.MainImageUpdateSpec();
        mainImageUpdateSpec.setUsername(hal9000.getOwner().getUsername());
        mainImageUpdateSpec.setFilename(hal9000.getUserFilename());
        updateSpec.setMainImage(mainImageUpdateSpec);
        final String body = this.objectMapper.writeValueAsString(updateSpec);

        final String accessToken = this.getAccessToken(owner);
        this.mockMvc.perform(
                post("/recipes/{username}/{slug}", owner.getUsername(), recipe.getSlug())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipe.mainImage").isMap());
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
