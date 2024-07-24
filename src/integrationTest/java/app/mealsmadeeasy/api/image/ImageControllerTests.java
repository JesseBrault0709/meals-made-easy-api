package app.mealsmadeeasy.api.image;

import app.mealsmadeeasy.api.auth.AuthService;
import app.mealsmadeeasy.api.auth.LoginException;
import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserCreateException;
import app.mealsmadeeasy.api.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.io.InputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class ImageControllerTests {

    private static final String USER_FILENAME = "HAL9000.svg";

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
        return ImageControllerTests.class.getResourceAsStream("HAL9000.svg");
    }

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private MockMvc mockMvc;

    private User createTestUser(String username) {
        try {
            return this.userService.createUser(username, username + "@test.com", "test");
        } catch (UserCreateException e) {
            throw new RuntimeException(e);
        }
    }

    private Image createHal9000(User owner) throws ImageException, IOException {
        try (final InputStream hal9000 = getHal9000()) {
            return this.imageService.create(
                    owner,
                    USER_FILENAME,
                    hal9000,
                    "image/svg+xml",
                    27881L
            );
        }
    }

    private String getAccessToken(String username) {
        try {
            return this.authService.login(username, "test").getAccessToken().getToken();
        } catch (LoginException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DirtiesContext
    public void getImageNoPrincipal() throws Exception {
        final User owner = this.createTestUser("imageOwner");
        final Image image = this.createHal9000(owner);
        this.imageService.setPublic(image, owner, true);
        try (final InputStream hal9000 = getHal9000()) {
            final byte[] halBytes = hal9000.readAllBytes();
            this.mockMvc.perform(get("/images/imageOwner/HAL9000.svg"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("image/svg+xml"))
                    .andExpect(content().bytes(halBytes));
        }
    }

    private void doGetImageTestWithViewer(String accessToken) throws Exception {
        try (final InputStream hal9000 = getHal9000()) {
            final byte[] halBytes = hal9000.readAllBytes();
            this.mockMvc.perform(get("/images/imageOwner/HAL9000.svg")
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("image/svg+xml"))
                    .andExpect(content().bytes(halBytes));
        }
    }

    @Test
    @DirtiesContext
    public void getImageWithOwner() throws Exception {
        final User owner = this.createTestUser("imageOwner");
        this.createHal9000(owner);
        final String accessToken = this.getAccessToken(owner.getUsername());
        this.doGetImageTestWithViewer(accessToken);
    }

    @Test
    @DirtiesContext
    public void getImageWithViewer() throws Exception {
        final User owner = this.createTestUser("imageOwner");
        final User viewer = this.createTestUser("viewer");
        final Image image = this.createHal9000(owner);
        this.imageService.addViewer(image, owner, viewer);
        final String accessToken = this.getAccessToken(viewer.getUsername());
        this.doGetImageTestWithViewer(accessToken);
    }

}
