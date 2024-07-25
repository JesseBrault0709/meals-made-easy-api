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

    private Image createHal9000(User owner) throws ImageException, IOException {
        try (final InputStream hal9000 = getHal9000()) {
            return this.imageService.create(
                    owner,
                    USER_FILENAME,
                    hal9000,
                    27881L
            );
        }
    }

    @Test
    public void smokeScreen() {}

    @Test
    @DirtiesContext
    public void simpleCreate() throws ImageException, IOException {
        final User owner = this.createTestUser("imageOwner");
        final Image image = this.createHal9000(owner);
        assertThat(image.getOwner(), isUser(owner));
        assertThat(image.getCreated(), is(notNullValue()));
        assertThat(image.getModified(), is(nullValue()));
        assertThat(image.getUserFilename(), is("HAL9000.svg"));
        assertThat(image.getMimeType(), is("image/svg+xml"));
        assertThat(image.getAlt(), is(nullValue()));
        assertThat(image.getCaption(), is(nullValue()));
        assertThat(image.isPublic(), is(false));
        assertThat(image.getViewers(), is(empty()));
    }

    @Test
    @DirtiesContext
    public void loadImageWithOwnerAsViewer() throws ImageException, IOException {
        final User owner = this.createTestUser("imageOwner");
        final Image image = this.createHal9000(owner);
        try (final InputStream stored =
                     this.imageService.getImageContent(image, owner)) {
            final byte[] storedBytes = stored.readAllBytes();
            assertThat(storedBytes.length, is(27881));
        }
    }

    @Test
    @DirtiesContext
    public void loadPublicImage() throws ImageException, IOException {
        final User owner = this.createTestUser("imageOwner");
        Image image = this.createHal9000(owner);
        image = this.imageService.setPublic(image, owner, true);
        try (final InputStream stored =
                     this.imageService.getImageContent(image, null)) {
            final byte[] storedBytes = stored.readAllBytes();
            assertThat(storedBytes.length, is(27881));
        }
    }

    @Test
    @DirtiesContext
    public void loadImageWithViewer() throws ImageException, IOException {
        final User owner = this.createTestUser("imageOwner");
        final User viewer = this.createTestUser("imageViewer");
        Image image = this.createHal9000(owner);
        image = this.imageService.addViewer(image, owner, viewer);
        try (final InputStream stored =
                     this.imageService.getImageContent(image, viewer)) {
            final byte[] storedBytes = stored.readAllBytes();
            assertThat(storedBytes.length, is(27881));
        }
    }

    @Test
    @DirtiesContext
    public void getImagesOwnedBy() throws ImageException, IOException {
        final User owner = this.createTestUser("imageOwner");
        final User otherOwner = this.createTestUser("otherImageOwner");
        final Image image0 = this.createHal9000(owner);
        final Image image1 = this.createHal9000(owner);
        final Image image2 = this.createHal9000(otherOwner);

        final List<Image> ownedImages = this.imageService.getImagesOwnedBy(owner);
        assertThat(ownedImages.size(), is(2));
        assertThat(ownedImages, containsImages(image0, image1));

        final List<Image> otherOwnedImages = this.imageService.getImagesOwnedBy(otherOwner);
        assertThat(otherOwnedImages.size(), is(1));
        assertThat(otherOwnedImages, containsImages(image2));
    }

    @Test
    @DirtiesContext
    public void updateOwner() throws ImageException, IOException {
        final User oldOwner = this.createTestUser("oldImageOwner");
        final User newOwner = this.createTestUser("newImageOwner");
        Image image = this.createHal9000(oldOwner);
        assertThat(image.getOwner(), isUser(oldOwner));
        image = this.imageService.updateOwner(image, oldOwner, newOwner);
        assertThat(image.getOwner(), isUser(newOwner));
    }

    @Test
    @DirtiesContext
    public void setAlt() throws ImageException, IOException {
        final User owner = this.createTestUser("imageOwner");
        Image image = this.createHal9000(owner);
        image = this.imageService.setAlt(image, owner, "Example alt.");
        assertThat(image.getAlt(), is("Example alt."));
    }

    @Test
    @DirtiesContext
    public void setCaption() throws ImageException, IOException {
        final User owner = this.createTestUser("imageOwner");
        Image image = this.createHal9000(owner);
        image = this.imageService.setCaption(image, owner, "Example caption.");
        assertThat(image.getCaption(), is("Example caption."));
    }

    @Test
    @DirtiesContext
    public void setPublicToTrue() throws ImageException, IOException {
        final User owner = this.createTestUser("imageOwner");
        Image image = this.createHal9000(owner);
        image = this.imageService.setPublic(image, owner, true);
        assertThat(image.isPublic(), is(true));
    }

}
