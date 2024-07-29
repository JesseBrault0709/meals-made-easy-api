package app.mealsmadeeasy.api.image;

import app.mealsmadeeasy.api.image.body.ImageUpdateInfoBody;
import app.mealsmadeeasy.api.image.spec.ImageCreateInfoSpec;
import app.mealsmadeeasy.api.image.spec.ImageUpdateInfoSpec;
import app.mealsmadeeasy.api.image.view.ImageView;
import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserService;
import app.mealsmadeeasy.api.util.AccessDeniedView;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;
    private final UserService userService;

    public ImageController(ImageService imageService, UserService userService) {
        this.imageService = imageService;
        this.userService = userService;
    }

    private ImageUpdateInfoSpec getImageUpdateSpec(ImageUpdateInfoBody body) {
        final ImageUpdateInfoSpec spec = new ImageUpdateInfoSpec();
        spec.setAlt(body.getAlt());
        spec.setCaption(body.getCaption());
        spec.setPublic(body.getPublic());
        if (body.getViewersToAdd() != null) {
            spec.setViewersToAdd(
                    body.getViewersToAdd().stream()
                            .map(this.userService::getUser)
                            .collect(Collectors.toSet())
            );
        }
        if (body.getViewersToRemove() != null) {
            spec.setViewersToRemove(
                    body.getViewersToRemove().stream()
                            .map(this.userService::getUser)
                            .collect(Collectors.toSet())
            );
        }
        spec.setClearAllViewers(body.getClearAllViewers());
        return spec;
    }

    @ExceptionHandler
    public ResponseEntity<AccessDeniedView> onAccessDenied(AccessDeniedException e) {
        if (e instanceof AuthorizationDeniedException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new AccessDeniedView(HttpStatus.FORBIDDEN.value(), e.getMessage()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new AccessDeniedView(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
        }
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
            @RequestParam(required = false) Set<String> viewers,
            @AuthenticationPrincipal User principal
    ) throws IOException, ImageException {
        if (principal == null) {
            throw new AccessDeniedException("Must be logged in.");
        }
        final ImageCreateInfoSpec createSpec = new ImageCreateInfoSpec();
        createSpec.setAlt(alt);
        createSpec.setCaption(caption);
        createSpec.setPublic(isPublic);
        if (viewers != null) {
            createSpec.setViewersToAdd(viewers.stream().map(this.userService::getUser).collect(Collectors.toSet()));
        }
        final Image saved = this.imageService.create(
                principal,
                filename,
                image.getInputStream(),
                image.getSize(),
                createSpec
        );
        return ResponseEntity.status(201).body(ImageUtil.toImageView(saved));
    }

    @PostMapping("/{username}/{filename}")
    public ResponseEntity<ImageView> updateInfo(
            @AuthenticationPrincipal User principal,
            @PathVariable String username,
            @PathVariable String filename,
            @RequestBody ImageUpdateInfoBody body
    ) throws ImageException {
        if (principal == null) {
            throw new AccessDeniedException("Must be logged in.");
        }
        final User owner = this.userService.getUser(username);
        final Image image = this.imageService.getByOwnerAndFilename(owner, filename, principal);
        final Image updated = this.imageService.update(image, principal, this.getImageUpdateSpec(body));
        return ResponseEntity.ok(ImageUtil.toImageView(updated));
    }

    @DeleteMapping("/{username}/{filename}")
    public ResponseEntity<Object> deleteImage(
            @AuthenticationPrincipal User principal,
            @PathVariable String username,
            @PathVariable String filename
    ) throws ImageException, IOException {
        if (principal == null) {
            throw new AccessDeniedException("Must be logged in.");
        }
        final User owner = this.userService.getUser(username);
        final Image image = this.imageService.getByOwnerAndFilename(owner, filename, principal);
        this.imageService.deleteImage(image, principal);
        return ResponseEntity.noContent().build();
    }

}
