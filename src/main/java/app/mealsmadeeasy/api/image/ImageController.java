package app.mealsmadeeasy.api.image;

import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/images")
public class ImageController {

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

}
