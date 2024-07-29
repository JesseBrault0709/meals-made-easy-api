package app.mealsmadeeasy.api.image;

import app.mealsmadeeasy.api.image.view.ImageView;
import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.view.UserInfoView;

import java.util.HashSet;
import java.util.Set;

public final class ImageUtil {

    public static ImageView toImageView(Image image) {
        final ImageView imageView = new ImageView();
        imageView.setCreated(image.getCreated());
        imageView.setModified(image.getModified());
        imageView.setFilename(image.getUserFilename());
        imageView.setMimeType(image.getMimeType());
        imageView.setAlt(image.getAlt());
        imageView.setCaption(image.getCaption());
        imageView.setIsPublic(image.isPublic());

        final User owner = image.getOwner();
        final UserInfoView userInfoView = new UserInfoView();
        userInfoView.setId(owner.getId());
        userInfoView.setUsername(owner.getUsername());
        imageView.setOwner(userInfoView);

        final Set<UserInfoView> viewers = new HashSet<>();
        for (final User viewer : image.getViewers()) {
            final UserInfoView viewerView = new UserInfoView();
            viewerView.setId(viewer.getId());
            viewerView.setUsername(viewer.getUsername());
            viewers.add(viewerView);
        }
        imageView.setViewers(viewers);

        return imageView;
    }

    private ImageUtil() {}

}
