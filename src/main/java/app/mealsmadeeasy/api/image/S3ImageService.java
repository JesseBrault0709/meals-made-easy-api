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
    private final ImageRepository imageRepository;
    private final String imageBucketName;

    public S3ImageService(
            S3Manager s3Manager,
            ImageRepository imageRepository,
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

        final ImageEntity draft = new ImageEntity();
        draft.setOwner((UserEntity) owner);
        draft.setUserFilename(userFilename);
        draft.setMimeType(mimeType);
        draft.setObjectName(objectName);
        draft.setInternalUrl(this.s3Manager.getUrl(this.imageBucketName, objectName));
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
    public InputStream getImageContentById(long id) throws IOException, ImageException {
        final Image image = this.getById(id);
        return this.s3Manager.load(this.imageBucketName, image.getObjectName());
    }

    @Override
    public InputStream getImageContentById(long id, User viewer) throws IOException, ImageException {
        final Image image = this.getById(id, viewer);
        return this.s3Manager.load(this.imageBucketName, image.getObjectName());
    }

    @Override
    public List<Image> getImagesOwnedBy(User user) {
        return new ArrayList<>(this.imageRepository.findAllByOwner((UserEntity) user));
    }

    @Override
    @PreAuthorize("@imageSecurity.isOwner(#image, #oldOwner)")
    public Image updateOwner(Image image, User oldOwner, User newOwner) {
        final ImageEntity imageEntity = (ImageEntity) image;
        imageEntity.setOwner((UserEntity) newOwner);
        return this.imageRepository.save(imageEntity);
    }

    @Override
    @PreAuthorize("@imageSecurity.isOwner(#image, #owner)")
    public Image setAlt(Image image, User owner, String alt) {
        final ImageEntity imageEntity = (ImageEntity) image;
        imageEntity.setAlt(alt);
        return this.imageRepository.save(imageEntity);
    }

    @Override
    @PreAuthorize("@imageSecurity.isOwner(#image, #owner)")
    public Image setCaption(Image image, User owner, String caption) {
        final ImageEntity imageEntity = (ImageEntity) image;
        imageEntity.setCaption(caption);
        return this.imageRepository.save(imageEntity);
    }

    @Override
    @PreAuthorize("@imageSecurity.isOwner(#image, #owner)")
    public Image setPublic(Image image, User owner, boolean isPublic) {
        final ImageEntity imageEntity = (ImageEntity) image;
        imageEntity.setPublic(isPublic);
        return this.imageRepository.save(imageEntity);
    }

    @Override
    @PreAuthorize("@imageSecurity.isOwner(#image, #owner)")
    public Image addViewer(Image image, User owner, User viewer) {
        final ImageEntity withViewers = this.imageRepository.getByIdWithViewers(image.getId());
        withViewers.getViewers().add((UserEntity) viewer);
        return this.imageRepository.save(withViewers);
    }

    @Override
    @PreAuthorize("@imageSecurity.isOwner(#image, #owner)")
    public Image removeViewer(Image image, User owner, User viewer) {
        final ImageEntity withViewers = this.imageRepository.getByIdWithViewers(image.getId());
        withViewers.getViewers().remove((UserEntity) viewer);
        return this.imageRepository.save(withViewers);
    }

    @Override
    @PreAuthorize("@imageSecurity.isOwner(#image, #owner)")
    public Image clearViewers(Image image, User owner) {
        final ImageEntity withViewers = this.imageRepository.getByIdWithViewers(image.getId());
        withViewers.getViewers().clear();
        return this.imageRepository.save(withViewers);
    }

    @Override
    @PreAuthorize("@imageSecurity.isOwner(#image, #owner)")
    public void deleteImage(Image image, User owner) throws IOException {
        this.imageRepository.delete((ImageEntity) image);
        this.s3Manager.delete("images", image.getObjectName());
    }

    @Override
    public void deleteById(long id, User owner) throws IOException {
        final ImageEntity toDelete = this.imageRepository.getReferenceById(id);
        this.deleteImage(toDelete, owner);
    }

}
