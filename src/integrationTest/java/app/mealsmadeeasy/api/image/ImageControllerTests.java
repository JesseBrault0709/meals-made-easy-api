package app.mealsmadeeasy.api.image;

import app.mealsmadeeasy.api.auth.AuthService;
import app.mealsmadeeasy.api.auth.LoginException;
import app.mealsmadeeasy.api.image.spec.ImageCreateInfoSpec;
import app.mealsmadeeasy.api.image.spec.ImageUpdateInfoSpec;
import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserCreateException;
import app.mealsmadeeasy.api.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
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
import java.util.Set;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
                    27881L,
                    new ImageCreateInfoSpec()
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

    private Image makePublic(Image image, User modifier) {
        final ImageUpdateInfoSpec spec = new ImageUpdateInfoSpec();
        spec.setPublic(true);
        return this.imageService.update(image, modifier, spec);
    }

    private Image addViewer(Image image, User modifier, User viewerToAdd) {
        final ImageUpdateInfoSpec spec = new ImageUpdateInfoSpec();
        spec.setViewersToAdd(Set.of(viewerToAdd));
        return this.imageService.update(image, modifier, spec);
    }

    @Test
    @DirtiesContext
    public void getImageNoPrincipal() throws Exception {
        final User owner = this.createTestUser("imageOwner");
        final Image image = this.createHal9000(owner);
        this.makePublic(image, owner);
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
        this.addViewer(image, owner, viewer);
        final String accessToken = this.getAccessToken(viewer.getUsername());
        this.doGetImageTestWithViewer(accessToken);
    }

    @Test
    @DirtiesContext
    public void putImage() throws Exception {
        final User owner = this.createTestUser("imageOwner");
        final String accessToken = this.getAccessToken(owner.getUsername());
        try (final InputStream hal9000 = getHal9000()) {
            final MockMultipartFile mockMultipartFile = new MockMultipartFile(
                    "image", "HAL9000.svg", "image/svg+xml", hal9000
            );
            this.mockMvc.perform(
                    multipart("/images")
                            .file(mockMultipartFile)
                            .param("filename", "HAL9000.svg")
                            .param("alt", "HAL 9000")
                            .param("caption", "HAL 9000, from 2001: A Space Odyssey")
                            .param("isPublic", "true")
                            .header("Authorization", "Bearer " + accessToken)
                            .with(req -> {
                                req.setMethod("PUT");
                                return req;
                            })
            )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.created").exists())
                    .andExpect(jsonPath("$.modified").value(nullValue()))
                    .andExpect(jsonPath("$.filename").value(USER_FILENAME))
                    .andExpect(jsonPath("$.mimeType").value("image/svg+xml"))
                    .andExpect(jsonPath("$.alt").value("HAL 9000"))
                    .andExpect(jsonPath("$.caption").value("HAL 9000, from 2001: A Space Odyssey"))
                    .andExpect(jsonPath("$.isPublic").value(true))
                    .andExpect(jsonPath("$.owner.username").value("imageOwner"))
                    .andExpect(jsonPath("$.owner.id").value(owner.getId()));
        }
    }

    @Test
    @DirtiesContext
    public void updateAlt() throws Exception {
        fail("TODO");
    }

    @Test
    @DirtiesContext
    public void updateCaption() throws Exception {
        fail("TODO");
    }

    @Test
    @DirtiesContext
    public void updateIsPublic() throws Exception {
        fail("TODO");
    }

    @Test
    @DirtiesContext
    public void addViewers() throws Exception {
        fail("TODO");
    }

    @Test
    @DirtiesContext
    public void removeViewers() throws Exception {
        fail("TODO");
    }

    @Test
    @DirtiesContext
    public void clearAllViewers() throws Exception {
        fail("TODO");
    }

    @Test
    @DirtiesContext
    public void updateInfoWithViewerFails() throws Exception {
        fail("TODO");
    }

    @Test
    @DirtiesContext
    public void deleteImageWithOwner() throws Exception {
        fail("TODO");
    }

    @Test
    @DirtiesContext
    public void deleteImageWithViewer() throws Exception {
        fail("TODO");
    }

}
