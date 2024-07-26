package app.mealsmadeeasy.api.image;

import app.mealsmadeeasy.api.image.spec.ImageCreateInfoSpec;
import app.mealsmadeeasy.api.image.spec.ImageUpdateInfoSpec;
import app.mealsmadeeasy.api.user.User;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ImageService {

    Image create(User owner, String userFilename, InputStream inputStream, long objectSize, ImageCreateInfoSpec infoSpec)
            throws IOException, ImageException;

    Image getByOwnerAndFilename(User owner, String filename, User viewer) throws ImageException;
    InputStream getImageContent(Image image, @Nullable User viewer) throws IOException;
    List<Image> getImagesOwnedBy(User user);

    Image update(Image image, User modifier, ImageUpdateInfoSpec spec);

    void deleteImage(Image image, User modifier) throws IOException;

}
