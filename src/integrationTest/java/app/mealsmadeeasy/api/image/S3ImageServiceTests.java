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
import java.util.List;

import static app.mealsmadeeasy.api.image.ContainsImagesMatcher.containsImages;
import static app.mealsmadeeasy.api.user.IsUserMatcher.isUser;
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

    private static InputStream getHal9000() {
        return S3ImageServiceTests.class.getResourceAsStream("HAL9000.svg");
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

    private Image createHal9000(User owner, InputStream data) throws ImageException, IOException {
        return this.imageService.create(
                owner,
                "HAL9000.svg",
                data,
                "image/svg+xml",
                27881L
        );
    }

    @Test
    public void smokeScreen() {}

    @Test
    @DirtiesContext
    public void simpleCreate() {
        try (final InputStream hal9000 = getHal9000()) {
            final User owner = this.createTestUser("imageOwner");
            final Image image = this.createHal9000(owner, hal9000);
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

    @Test
    @DirtiesContext
    public void loadImageWithOwner() {
        try (final InputStream hal9000 = getHal9000()) {
            final User owner = this.createTestUser("imageOwner");
            final Image image = this.createHal9000(owner, hal9000);
            try (final InputStream stored = this.imageService.getImageContentById(image.getId(), owner)) {
                final byte[] storedBytes = stored.readAllBytes();
                assertThat(storedBytes.length, is(27881));
            }
        } catch (IOException | ImageException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void loadPublicImage() {
        try (final InputStream hal9000 = getHal9000()) {
            final User owner = this.createTestUser("imageOwner");
            Image image = this.createHal9000(owner, hal9000);
            image = this.imageService.setPublic(image, owner, true);
            try (final InputStream stored = this.imageService.getImageContentById(image.getId())) {
                final byte[] storedBytes = stored.readAllBytes();
                assertThat(storedBytes.length, is(27881));
            }
        } catch (IOException | ImageException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DirtiesContext
    public void loadImageWithViewer() {
        try (final InputStream hal9000 = getHal9000()) {
            final User owner = this.createTestUser("imageOwner");
            final User viewer = this.createTestUser("imageViewer");
            Image image = this.createHal9000(owner, hal9000);
            image = this.imageService.addViewer(image, owner, viewer);
            try (final InputStream stored = this.imageService.getImageContentById(image.getId(), viewer)) {
                final byte[] storedBytes = stored.readAllBytes();
                assertThat(storedBytes.length, is(27881));
            }
        } catch (IOException | ImageException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DirtiesContext
    public void getImagesOwnedBy() {
        try (
                final InputStream hal9000_0 = getHal9000();
                final InputStream hal9000_1 = getHal9000();
                final InputStream hal9000_2 = getHal9000();
                ) {
            final User owner = this.createTestUser("imageOwner");
            final User otherOwner = this.createTestUser("otherImageOwner");
            final Image image0 = this.createHal9000(owner, hal9000_0);
            final Image image1 = this.createHal9000(owner, hal9000_1);
            final Image image2 = this.createHal9000(otherOwner, hal9000_2);

            final List<Image> ownedImages = this.imageService.getImagesOwnedBy(owner);
            assertThat(ownedImages.size(), is(2));
            assertThat(ownedImages, containsImages(image0, image1));

            final List<Image> otherOwnedImages = this.imageService.getImagesOwnedBy(otherOwner);
            assertThat(otherOwnedImages.size(), is(1));
            assertThat(otherOwnedImages, containsImages(image2));
        } catch (IOException | ImageException e) {
            throw new RuntimeException(e);
        }
    }



}
