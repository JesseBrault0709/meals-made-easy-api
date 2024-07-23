package app.mealsmadeeasy.api.image;

import app.mealsmadeeasy.api.user.User;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("imageSecurity")
public class ImageSecurityImpl implements ImageSecurity {

    private final S3ImageRepository imageRepository;

    public ImageSecurityImpl(S3ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public boolean isViewableBy(Image image, @Nullable User viewer) {
        if (image.isPublic()) {
            // public image
            return true;
        } else if (viewer == null) {
            // non-public and no principal
            return false;
        } else if (Objects.equals(image.getOwner().getId(), viewer.getId())) {
            // is owner
            return true;
        } else {
            // check if viewer
            final S3ImageEntity withViewers = this.imageRepository.getByIdWithViewers(image.getId());
            for (final User user : withViewers.getViewers()) {
                if (user.getId() != null && user.getId().equals(viewer.getId())) {
                    return true;
                }
            }
        }
        // non-public and not viewer
        return false;
    }

    @Override
    public boolean isOwner(Image image, @Nullable User user) {
        return image.getOwner() != null && user != null && Objects.equals(image.getOwner().getId(), user.getId());
    }

}
