package app.mealsmadeeasy.api.image;

import app.mealsmadeeasy.api.image.spec.ImageCreateInfoSpec;
import app.mealsmadeeasy.api.image.spec.ImageUpdateInfoSpec;
import app.mealsmadeeasy.api.image.view.ImageView;
import app.mealsmadeeasy.api.s3.S3Manager;
import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserEntity;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class S3ImageService implements ImageService {

    private static final Pattern extensionPattern = Pattern.compile(".+\\.(.+)$");

    private final S3Manager s3Manager;
    private final S3ImageRepository imageRepository;
    private final String imageBucketName;
    private final String baseUrl;

    public S3ImageService(
            S3Manager s3Manager,
            S3ImageRepository imageRepository,
            @Value("${app.mealsmadeeasy.api.images.bucketName}") String imageBucketName,
            @Value("${app.mealsmadeeasy.api.baseUrl}") String baseUrl
    ) {
        this.s3Manager = s3Manager;
        this.imageRepository = imageRepository;
        this.imageBucketName = imageBucketName;
        this.baseUrl = baseUrl;
    }

    private String getMimeType(String userFilename) {
        final Matcher m = extensionPattern.matcher(userFilename);
        if (m.matches()) {
            final String extension = m.group(1);
            return switch (extension) {
                case "jpg", "jpeg" -> "image/jpeg";
                case "png" -> "image/png";
                case "svg" -> "image/svg+xml";
                case "webp" -> "image/webp";
                default -> throw new IllegalArgumentException("Cannot determine mime type for extension: " + extension);
            };
        } else {
            throw new IllegalArgumentException("Cannot determine mime type for filename: " + userFilename);
        }
    }

    private String getExtension(String mimeType) throws ImageException {
        return switch (mimeType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/svg+xml" -> "svg";
            case "image/webp" -> "webp";
            default -> throw new ImageException(
                    ImageException.Type.UNKNOWN_MIME_TYPE,
                    "Unknown mime type: " + mimeType
            );
        };
    }

    private boolean transferFromSpec(S3ImageEntity entity, ImageCreateInfoSpec spec) {
        boolean didTransfer = false;
        if (spec.getAlt() != null) {
            entity.setAlt(spec.getAlt());
            didTransfer = true;
        }
        if (spec.getCaption() != null) {
            entity.setCaption(spec.getCaption());
            didTransfer = true;
        }
        if (spec.getPublic() != null) {
            entity.setPublic(spec.getPublic());
            didTransfer = true;
        }
        final @Nullable Set<User> viewersToAdd = spec.getViewersToAdd();
        if (viewersToAdd != null) {
            final Set<UserEntity> viewers = new HashSet<>(entity.getViewerEntities());
            for (final User viewerToAdd : spec.getViewersToAdd()) {
                viewers.add((UserEntity) viewerToAdd);
            }
            entity.setViewers(viewers);
            didTransfer = true;
        }
        return didTransfer;
    }

    @Override
    public Image create(
            User owner,
            String userFilename,
            InputStream inputStream,
            long objectSize,
            ImageCreateInfoSpec createSpec
    ) throws IOException, ImageException {
        final String mimeType = this.getMimeType(userFilename);
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
        this.transferFromSpec(draft, createSpec);
        return this.imageRepository.save(draft);
    }

    @Override
    @PostAuthorize("@imageSecurity.isViewableBy(returnObject, #viewer)")
    public Image getById(long id, @Nullable User viewer) throws ImageException {
        return this.imageRepository.findById(id).orElseThrow(() -> new ImageException(
                ImageException.Type.INVALID_ID, "No Image with id: " + id
        ));
    }

    @Override
    @PostAuthorize("@imageSecurity.isViewableBy(returnObject, #viewer)")
    public Image getByOwnerAndFilename(User owner, String filename, User viewer) throws ImageException {
        return this.imageRepository.findByOwnerAndUserFilename((UserEntity) owner, filename)
                .orElseThrow(() -> new ImageException(
                        ImageException.Type.IMAGE_NOT_FOUND,
                        "No such image for owner " + owner + " with filename " + filename
                ));
    }

    @Override
    @PostAuthorize("@imageSecurity.isViewableBy(returnObject, #viewer)")
    public Image getByUsernameAndFilename(String username, String filename, User viewer) throws ImageException {
        return this.imageRepository.findByOwnerUsernameAndFilename(username, filename).orElseThrow(
                () -> new ImageException(
                        ImageException.Type.INVALID_USERNAME_OR_FILENAME,
                        "No such Image for username " + username + " and filename " + filename
                )
        );
    }

    @Override
    @PreAuthorize("@imageSecurity.isViewableBy(#image, #viewer)")
    public InputStream getImageContent(Image image, User viewer) throws IOException {
        return this.s3Manager.load(this.imageBucketName, ((S3ImageEntity) image).getObjectName());
    }

    @Override
    public List<Image> getImagesOwnedBy(User user) {
        return new ArrayList<>(this.imageRepository.findAllByOwner((UserEntity) user));
    }

    @Override
    @PreAuthorize("@imageSecurity.isOwner(#image, #modifier)")
    public Image update(final Image image, User modifier, ImageUpdateInfoSpec updateSpec) {
        S3ImageEntity entity = (S3ImageEntity) image;
        boolean didUpdate = this.transferFromSpec(entity, updateSpec);
        final @Nullable Boolean clearAllViewers = updateSpec.getClearAllViewers();
        if (clearAllViewers != null && clearAllViewers) {
            entity.setViewers(new HashSet<>());
            didUpdate = true;
        } else {
            final @Nullable Set<User> viewersToRemove = updateSpec.getViewersToRemove();
            if (viewersToRemove != null) {
                final Set<UserEntity> currentViewers = new HashSet<>(entity.getViewerEntities());
                for (final User toRemove : updateSpec.getViewersToRemove()) {
                    currentViewers.remove((UserEntity) toRemove);
                }
                entity.setViewers(currentViewers);
                didUpdate = true;
            }
        }
        if (didUpdate) {
            entity.setModified(LocalDateTime.now());
        }
        return this.imageRepository.save(entity);
    }

    @Override
    @PreAuthorize("@imageSecurity.isOwner(#image, #modifier)")
    public void deleteImage(Image image, User modifier) throws IOException {
        final S3ImageEntity imageEntity = (S3ImageEntity) image;
        this.imageRepository.delete(imageEntity);
        this.s3Manager.delete("images", imageEntity.getObjectName());
    }

    private String getImageUrl(Image image) {
        return this.baseUrl + "/images/" + image.getOwner().getUsername() + "/" + image.getUserFilename();
    }

    @Override
    public ImageView toImageView(Image image, @Nullable User viewer) {
        if (viewer != null && image.getOwner().getUsername().equals(viewer.getUsername())) {
            return ImageView.from(image, this.getImageUrl(image), true);
        } else {
            return ImageView.from(image, this.getImageUrl(image), false);
        }
    }

}
