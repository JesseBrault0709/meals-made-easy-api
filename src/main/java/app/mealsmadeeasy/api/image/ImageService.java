package app.mealsmadeeasy.api.image;

import app.mealsmadeeasy.api.user.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ImageService {

    Image create(User owner, String userFilename, InputStream inputStream, String mimeType, long objectSize)
            throws IOException, ImageException;

    Image getById(long id) throws ImageException;
    Image getById(long id, User viewer) throws ImageException;
    Image getByOwnerAndFilename(User viewer, User owner, String filename) throws ImageException;

    InputStream getImageContentByOwnerAndFilename(User owner, String filename) throws ImageException, IOException;
    InputStream getImageContentByOwnerAndFilename(User viewer, User owner, String filename) throws ImageException, IOException;

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
