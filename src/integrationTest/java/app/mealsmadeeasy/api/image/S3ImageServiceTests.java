package app.mealsmadeeasy.api.image;

import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserCreateException;
import app.mealsmadeeasy.api.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.io.InputStream;

import static app.mealsmadeeasy.api.matchers.Matchers.isUser;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.notNullValue;

@Testcontainers
@SpringBootTest
public class S3ImageServiceTests {

    @Container
    private static final MinIOContainer container = new MinIOContainer(
            DockerImageName.parse("minio/minio:latest")
    );

    @DynamicPropertySource
    public static void minioProperties(DynamicPropertyRegistry registry) {
        registry.add("app.mealsmadeeasy.api.minio.bucketName", () -> "test-bucket");
        registry.add("app.mealsmadeeasy.api.minio.endpoint", container::getS3URL);
        registry.add("app.mealsmadeeasy.api.minio.accessKey", container::getUserName);
        registry.add("app.mealsmadeeasy.api.minio.secretKey", container::getPassword);
    }

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    private User createTestUser(String username) {
        try {
            return this.userService.createUser(username, username + "@test.com", "test");
        } catch (UserCreateException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void smokeScreen() {}

    @Test
    @DirtiesContext
    public void simpleCreate() {
        try (final InputStream hal9000 = S3ImageServiceTests.class.getResourceAsStream("HAL9000.svg")) {
            final User owner = this.createTestUser("imageOwner");
            final Image image = this.imageService.create(
                    owner,
                    "HAL9000.svg",
                    hal9000,
                    "image/svg+xml",
                    27881L
            );
            assertThat(image.getOwner(), isUser(owner));
            assertThat(image.getCreated(), is(notNullValue()));
            assertThat(image.getModified(), is(nullValue()));
            assertThat(image.getUserFilename(), is("HAL9000.svg"));
            assertThat(image.getMimeType(), is("image/svg+xml"));
            assertThat(image.getAlt(), is(nullValue()));
            assertThat(image.getCaption(), is(nullValue()));
            assertThat(image.getInternalUrl(), is(notNullValue()));
            assertThat(image.isPublic(), is(false));
            assertThat(image.getViewers(), is(empty()));
        } catch (IOException | ImageException e) {
            throw new RuntimeException(e);
        }
    }

}
