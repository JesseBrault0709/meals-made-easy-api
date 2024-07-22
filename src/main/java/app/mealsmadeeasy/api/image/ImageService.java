package app.mealsmadeeasy.api.image;

import app.mealsmadeeasy.api.user.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ImageService {

    Image create(User owner, String userFilename, InputStream inputStream, String mimeType, long objectSize)
            throws IOException, ImageException;

    Image getById(long id);
    Image getById(long id, User viewer);

    List<Image> getImagesOwnedBy(User user);

    Image updateOwner(Image image, User oldOwner, User newOwner);

    Image setAlt(Image image, User owner, String alt);
    Image setCaption(Image image, User owner, String caption);
    Image setPublic(Image image, User owner, boolean isPublic);

    void deleteImage(Image image, User owner) throws IOException;
    void deleteById(long id, User owner) throws IOException;

}
