package app.mealsmadeeasy.api.image;

import app.mealsmadeeasy.api.image.view.ImageView;
import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserService;
import app.mealsmadeeasy.api.user.view.UserInfoView;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/images")
public class ImageController {

    private static ImageView getView(Image image, User owner) {
        final ImageView imageView = new ImageView();
        imageView.setCreated(image.getCreated());
        imageView.setFilename(image.getUserFilename());
        imageView.setMimeType(image.getMimeType());
        imageView.setAlt(image.getAlt());
        imageView.setCaption(image.getCaption());
        imageView.setIsPublic(image.isPublic());

        final UserInfoView userInfoView = new UserInfoView();
        userInfoView.setId(owner.getId());
        userInfoView.setUsername(owner.getUsername());
        imageView.setOwner(userInfoView);

        return imageView;
    }

    private final ImageService imageService;
    private final UserService userService;

    public ImageController(ImageService imageService, UserService userService) {
        this.imageService = imageService;
        this.userService = userService;
    }

    @GetMapping("/{username}/{filename}")
    public ResponseEntity<InputStreamResource> getImage(
            @AuthenticationPrincipal User principal,
            @PathVariable String username,
            @PathVariable String filename
    ) throws ImageException, IOException {
        final User owner = this.userService.getUser(username);
        final Image image = this.imageService.getByOwnerAndFilename(owner, filename, principal);
        final InputStream imageInputStream = this.imageService.getImageContent(image, principal);
        return ResponseEntity.status(200)
                .contentType(MediaType.parseMediaType(image.getMimeType()))
                .body(new InputStreamResource(imageInputStream));
    }

    @PutMapping
    public ResponseEntity<ImageView> putImage(
            @RequestParam MultipartFile image,
            @RequestParam String filename,
            @RequestParam(required = false) String alt,
            @RequestParam(required = false) String caption,
            @RequestParam(required = false) Boolean isPublic,
            @AuthenticationPrincipal User principal
    ) throws IOException, ImageException {
        if (principal == null) {
            throw new AccessDeniedException("Must be logged in.");
        }

        Image saved = this.imageService.create(
                principal,
                filename,
                image.getInputStream(),
                image.getSize()
        );
        if (alt != null) {
            saved = this.imageService.setAlt(saved, principal, alt);
        }
        if (caption != null) {
            saved = this.imageService.setCaption(saved, principal, caption);
        }
        if (isPublic != null) {
            saved = this.imageService.setPublic(saved, principal, isPublic);
        }

        return ResponseEntity.status(201).body(getView(saved, principal));
    }

}
