package app.mealsmadeeasy.api.s3;

import io.minio.*;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
public class MinioS3Manager implements S3Manager {

    @Value("${app.mealsmadeeasy.api.minio.endpoint}")
    private String endpoint;

    @Value("${app.mealsmadeeasy.api.minio.accessKey}")
    private String accessKey;

    @Value("${app.mealsmadeeasy.api.minio.secretKey}")
    private String secretKey;

    @Override
    public String store(
            String bucketName,
            String filename,
            String mimeType,
            InputStream inputStream,
            long objectSize
    ) throws IOException {
        try (final MinioClient client = MinioClient.builder()
                .endpoint(this.endpoint)
                .credentials(this.accessKey, this.secretKey)
                .build()) {
            final boolean bucketExists = client.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            if (!bucketExists) {
                client.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
            }

            final ObjectWriteResponse response = client.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .stream(inputStream, objectSize, -1)
                            .contentType(mimeType)
                            .object(filename)
                            .build()
            );
            return response.object();
        } catch (ErrorResponseException | XmlParserException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException e) {
            throw new IOException(e);
        } catch (Exception minioBuildException) {
            throw new RuntimeException(minioBuildException);
        }
    }

    @Override
    public void delete(String bucketName, String objectName) throws IOException {
        try (final MinioClient client = MinioClient.builder()
                .endpoint(this.endpoint)
                .credentials(this.accessKey, this.secretKey)
                .build()) {
            client.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException e) {
            throw new IOException(e);
        } catch (Exception minioBuildException) {
            throw new RuntimeException(minioBuildException);
        }
    }

    @Override
    public String getUrl(String bucketName, String objectName) {
        return this.endpoint + "/" + bucketName + "/" + objectName;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKey() {
        return this.accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return this.secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

}
