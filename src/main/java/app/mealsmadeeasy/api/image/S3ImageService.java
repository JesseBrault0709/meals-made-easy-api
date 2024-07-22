package app.mealsmadeeasy.api.image;

import app.mealsmadeeasy.api.s3.S3Manager;
import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
public class S3ImageService implements ImageService {

    private final S3Manager s3Manager;
    private final ImageRepository imageRepository;

    public S3ImageService(S3Manager s3Manager, ImageRepository imageRepository) {
        this.s3Manager = s3Manager;
        this.imageRepository = imageRepository;
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
        final String objectName = this.s3Manager.store("images", filename, mimeType, inputStream, objectSize);

        final ImageEntity draft = new ImageEntity();
        draft.setOwner((UserEntity) owner);
        draft.setUserFilename(userFilename);
        draft.setMimeType(mimeType);
        draft.setObjectName(objectName);
        draft.setInternalUrl(this.s3Manager.getUrl("images", objectName));
        return this.imageRepository.save(draft);
    }

    @Override
    @PostAuthorize("returnObject.isPublic")
    public Image getById(long id) {
        return this.imageRepository.getReferenceById(id);
    }

    @Override
    @PostAuthorize("@imageSecurity.isViewableBy(returnObject, #viewer)")
    public Image getById(long id, User viewer) {
        return this.imageRepository.getReferenceById(id);
    }

    @Override
    public List<Image> getImagesOwnedBy(User user) {
        return List.of();
    }

    @Override
    public Image updateOwner(Image image, User oldOwner, User newOwner) {
        return null;
    }

    @Override
    public Image setAlt(Image image, User owner, String alt) {
        return null;
    }

    @Override
    public Image setCaption(Image image, User owner, String caption) {
        return null;
    }

    @Override
    public Image setPublic(Image image, User owner, boolean isPublic) {
        return null;
    }

    @Override
    @PreAuthorize("@imageSecurity.isOwner(image, owner)")
    public void deleteImage(Image image, User owner) throws IOException {
        this.imageRepository.delete((ImageEntity) image);
        this.s3Manager.delete("images", image.getObjectName()); // TODO
    }

    @Override
    public void deleteById(long id, User owner) throws IOException {
        final ImageEntity toDelete = this.imageRepository.getReferenceById(id);
        this.deleteImage(toDelete, owner);
    }

}
