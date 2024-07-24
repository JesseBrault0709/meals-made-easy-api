package app.mealsmadeeasy.api.image;

import app.mealsmadeeasy.api.user.User;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ImageService {

    Image create(User owner, String userFilename, InputStream inputStream, String mimeType, long objectSize)
            throws IOException, ImageException;

    Image getByOwnerAndFilename(User owner, String filename, User viewer) throws ImageException;
    InputStream getImageContent(Image image, @Nullable User viewer) throws IOException;
    List<Image> getImagesOwnedBy(User user);

    Image updateOwner(Image image, User oldOwner, User newOwner);

    Image setAlt(Image image, User owner, String alt);
    Image setCaption(Image image, User owner, String caption);
    Image setPublic(Image image, User owner, boolean isPublic);

    Image addViewer(Image image, User owner, User viewer);
    Image removeViewer(Image image, User owner, User viewer);
    Image clearViewers(Image image, User owner);

    void deleteImage(Image image, User owner) throws IOException;

}
