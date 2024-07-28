package app.mealsmadeeasy.api;

import app.mealsmadeeasy.api.image.Image;
import app.mealsmadeeasy.api.image.ImageException;
import app.mealsmadeeasy.api.image.ImageService;
import app.mealsmadeeasy.api.image.spec.ImageCreateInfoSpec;
import app.mealsmadeeasy.api.recipe.Recipe;
import app.mealsmadeeasy.api.recipe.RecipeService;
import app.mealsmadeeasy.api.recipe.spec.RecipeCreateSpec;
import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Configuration
@Profile("dev")
public class DevConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DevConfiguration.class);

    private final UserService userService;
    private final RecipeService recipeService;
    private final ImageService imageService;

    public DevConfiguration(UserService userService, RecipeService recipeService, ImageService imageService) {
        this.userService = userService;
        this.recipeService = recipeService;
        this.imageService = imageService;
    }

    @Bean
    public CommandLineRunner addTestData() {
        return args -> {
            final User testUser = this.userService.createUser(
                    "test", "test@test.com", "test", Set.of()
            );
            logger.info("Created {}", testUser);

            final ImageCreateInfoSpec obazdaCreateSpec = new ImageCreateInfoSpec();
            obazdaCreateSpec.setAlt("Obazda");
            obazdaCreateSpec.setCaption("German Obazda.");
            obazdaCreateSpec.setPublic(true);
            final Image obazdaImage;
            try (final InputStream obazdaStream = DevConfiguration.class.getResourceAsStream("Obazda.jpg")) {
                obazdaImage = this.imageService.create(
                        testUser,
                        "Obazda.jpg",
                        obazdaStream,
                        48654L,
                        obazdaCreateSpec
                );
            }
            logger.info("Created {}", obazdaImage);

            final RecipeCreateSpec recipeCreateSpec = new RecipeCreateSpec();
            recipeCreateSpec.setTitle("Test Recipe");
            recipeCreateSpec.setRawText("Hello, World!");
            recipeCreateSpec.setPublic(true);
            recipeCreateSpec.setMainImage(obazdaImage);
            final Recipe recipe = this.recipeService.create(testUser, recipeCreateSpec);
            logger.info("Created {}", recipe);

            try (final InputStream inputStream = DevConfiguration.class.getResourceAsStream("HAL9000.svg")) {
                final ImageCreateInfoSpec imageCreateSpec = new ImageCreateInfoSpec();
                imageCreateSpec.setPublic(true);
                final Image image = this.imageService.create(
                        testUser,
                        "HAL9000.svg",
                        inputStream,
                        27881L,
                        imageCreateSpec
                );
                logger.info("Created {}", image);
            } catch (IOException | ImageException e) {
                logger.error("Failed to load and/or create HAL9000.svg", e);
            }
        };
    }

}
