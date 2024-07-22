package app.mealsmadeeasy.api.s3;

import java.io.IOException;
import java.io.InputStream;

public interface S3Manager {

    InputStream load(
            String bucket,
            String objectName
    ) throws IOException;

    /**
     * @param bucket the target bucket in which to store the content
     * @param filename the filename to store, usually a uuid + appropriate extension
     * @param mimeType the mimeType of the content
     * @param inputStream the content
     * @param size the size of the content
     * @return the object name
     * @throws IOException if there is an exception with the backing service
     */
    String store(
            String bucket,
            String filename,
            String mimeType,
            InputStream inputStream,
            long size
    ) throws IOException;

    void delete(String bucketName, String objectName) throws IOException;

    String getUrl(String bucketName, String objectName);

}
