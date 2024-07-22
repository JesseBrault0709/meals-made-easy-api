package app.mealsmadeeasy.api.s3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class MinioS3ManagerTests {

    @Container
    private static final MinIOContainer container = new MinIOContainer(
            DockerImageName.parse("minio/minio:latest")
    );

    private MinioS3Manager s3Manager;

    @BeforeEach
    public void beforeEach() {
        this.s3Manager = new MinioS3Manager();
        this.s3Manager.setEndpoint(container.getS3URL());
        this.s3Manager.setAccessKey(container.getUserName());
        this.s3Manager.setSecretKey(container.getPassword());
    }

    @Test
    public void smokeScreen() {}

    @Test
    public void simpleStore() {
        try (final InputStream hal9000 = MinioS3ManagerTests.class.getResourceAsStream("HAL9000.svg")) {
            final String objectName = this.s3Manager.store(
                    "test-images",
                    "HAL9000.svg",
                    "image/svg+xml",
                    hal9000,
                    27881L
            );
            assertEquals("HAL9000.svg", objectName);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    @Test
    public void simpleLoad() {
        try (final InputStream hal9000 = MinioS3ManagerTests.class.getResourceAsStream("HAL9000.svg")) {
            if (hal9000 == null) {
                throw new RuntimeException("HAL9000.svg could not be found");
            }
            final String objectName = this.s3Manager.store(
                    "test-images",
                    "HAL9000.svg",
                    "image/svg+xml",
                    hal9000,
                    27881L
            );
            try (final InputStream loadedObject = this.s3Manager.load("test-images", objectName)) {
                final byte[] stored = loadedObject.readAllBytes();
                assertEquals(27881L, stored.length);
            }
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    @Test
    public void simpleDelete() {
        try (final InputStream hal9000 = MinioS3ManagerTests.class.getResourceAsStream("HAL9000.svg")) {
            final String objectName = this.s3Manager.store(
                    "test-images",
                    "HAL9000.svg",
                    "image/svg+xml",
                    hal9000,
                    27881L
            );
            this.s3Manager.delete("test-images", objectName);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

}
