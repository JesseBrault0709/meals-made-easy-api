package app.mealsmadeeasy.api.image;

import app.mealsmadeeasy.api.s3.S3Manager;
import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class S3ImageService implements ImageService {

    private final S3Manager s3Manager;
    private final S3ImageRepository imageRepository;
    private final String imageBucketName;

    public S3ImageService(
            S3Manager s3Manager,
            S3ImageRepository imageRepository,
            @Value("${app.mealsmadeeasy.api.images.bucketName}") String imageBucketName
    ) {
        this.s3Manager = s3Manager;
        this.imageRepository = imageRepository;
        this.imageBucketName = imageBucketName;
    }

    private String getExtension(String mimeType) throws ImageException {
        return switch (mimeType) {
            case "image/svg+xml" -> "svg";
            default -> throw new ImageException(
                    ImageException.Type.UNKNOWN_MIME_TYPE,
                    "unknown mime type: " + mimeType
            );
        };
    }

    @Override
    public Image create(User owner, String userFilename, InputStream inputStream, String mimeType, long objectSize)
            throws IOException, ImageException {
        final String uuid = UUID.randomUUID().toString();
        final String extension = this.getExtension(mimeType);
        final String filename = uuid + "." + extension;
        final String objectName = this.s3Manager.store(
                this.imageBucketName, filename, mimeType, inputStream, objectSize
        );

        final S3ImageEntity draft = new S3ImageEntity();
        draft.setOwner((UserEntity) owner);
        draft.setUserFilename(userFilename);
        draft.setMimeType(mimeType);
        draft.setObjectName(objectName);
        return this.imageRepository.save(draft);
    }

    @Override
    @PostAuthorize("returnObject.isPublic")
    public Image getById(long id) throws ImageException {
        return this.imageRepository.findById(id).orElseThrow(() -> new ImageException(
                ImageException.Type.INVALID_ID, "No such image with id " + id
        ));
    }

    @Override
    @PostAuthorize("@imageSecurity.isViewableBy(returnObject, #viewer)")
    public Image getById(long id, User viewer) throws ImageException {
        return this.imageRepository.findById(id).orElseThrow(() -> new ImageException(
                ImageException.Type.INVALID_ID, "No such image with id " + id
        ));
    }

    @Override
    @PostAuthorize("@imageSecurity.isViewableBy(returnObject, #viewer)")
    public Image getByOwnerAndFilename(User viewer, User owner, String filename) throws ImageException {
        return this.imageRepository.findByOwnerAndUserFilename((UserEntity) owner, filename)
                .orElseThrow(() -> new ImageException(
                        ImageException.Type.IMAGE_NOT_FOUND,
                        "No such image for owner " + owner + " with filename " + filename
                ));
    }

    @Override
    public InputStream getImageContentByOwnerAndFilename(User viewer, User owner, String filename)
            throws ImageException, IOException {
        final S3ImageEntity imageEntity = (S3ImageEntity) this.getByOwnerAndFilename(viewer, owner, filename);
        return this.s3Manager.load(this.imageBucketName, imageEntity.getObjectName());
    }

    @Override
    public InputStream getImageContentByOwnerAndFilename(User owner, String filename) throws ImageException, IOException {
        final S3ImageEntity imageEntity = (S3ImageEntity) this.getByOwnerAndFilename(null, owner, filename);
        return this.s3Manager.load(this.imageBucketName, imageEntity.getObjectName());
    }

    @Override
    public List<Image> getImagesOwnedBy(User user) {
        return new ArrayList<>(this.imageRepository.findAllByOwner((UserEntity) user));
    }

    @Override
    @PreAuthorize("@imageSecurity.isOwner(#image, #oldOwner)")
    public Image updateOwner(Image image, User oldOwner, User newOwner) {
        final S3ImageEntity imageEntity = (S3ImageEntity) image;
        imageEntity.setOwner((UserEntity) newOwner);
        return this.imageRepository.save(imageEntity);
    }

    @Override
    @PreAuthorize("@imageSecurity.isOwner(#image, #owner)")
    public Image setAlt(Image image, User owner, String alt) {
        final S3ImageEntity imageEntity = (S3ImageEntity) image;
        imageEntity.setAlt(alt);
        return this.imageRepository.save(imageEntity);
    }

    @Override
    @PreAuthorize("@imageSecurity.isOwner(#image, #owner)")
    public Image setCaption(Image image, User owner, String caption) {
        final S3ImageEntity imageEntity = (S3ImageEntity) image;
        imageEntity.setCaption(caption);
        return this.imageRepository.save(imageEntity);
    }

    @Override
    @PreAuthorize("@imageSecurity.isOwner(#image, #owner)")
    public Image setPublic(Image image, User owner, boolean isPublic) {
        final S3ImageEntity imageEntity = (S3ImageEntity) image;
        imageEntity.setPublic(isPublic);
        return this.imageRepository.save(imageEntity);
    }

    @Override
    @PreAuthorize("@imageSecurity.isOwner(#image, #owner)")
    public Image addViewer(Image image, User owner, User viewer) {
        final S3ImageEntity withViewers = this.imageRepository.getByIdWithViewers(image.getId());
        withViewers.getViewers().add((UserEntity) viewer);
        return this.imageRepository.save(withViewers);
    }

    @Override
    @PreAuthorize("@imageSecurity.isOwner(#image, #owner)")
    public Image removeViewer(Image image, User owner, User viewer) {
        final S3ImageEntity withViewers = this.imageRepository.getByIdWithViewers(image.getId());
        withViewers.getViewers().remove((UserEntity) viewer);
        return this.imageRepository.save(withViewers);
    }

    @Override
    @PreAuthorize("@imageSecurity.isOwner(#image, #owner)")
    public Image clearViewers(Image image, User owner) {
        final S3ImageEntity withViewers = this.imageRepository.getByIdWithViewers(image.getId());
        withViewers.getViewers().clear();
        return this.imageRepository.save(withViewers);
    }

    @Override
    @PreAuthorize("@imageSecurity.isOwner(#image, #owner)")
    public void deleteImage(Image image, User owner) throws IOException {
        final S3ImageEntity imageEntity = (S3ImageEntity) image;
        this.imageRepository.delete(imageEntity);
        this.s3Manager.delete("images", imageEntity.getObjectName());
    }

}
