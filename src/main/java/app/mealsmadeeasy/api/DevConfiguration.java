package app.mealsmadeeasy.api;

import app.mealsmadeeasy.api.image.Image;
import app.mealsmadeeasy.api.image.ImageException;
import app.mealsmadeeasy.api.image.ImageService;
import app.mealsmadeeasy.api.recipe.Recipe;
import app.mealsmadeeasy.api.recipe.RecipeService;
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

            final Recipe recipe = this.recipeService.create(testUser, "Test Recipe", "Hello, World!");
            this.recipeService.setPublic(recipe, testUser, true);
            logger.info("Created {}", recipe);

            try (final InputStream inputStream = DevConfiguration.class.getResourceAsStream("HAL9000.svg")) {
                final Image image = this.imageService.create(
                        testUser,
                        "HAL9000.svg",
                        inputStream,
                        27881L
                );
                this.imageService.setPublic(image, testUser, true);
                logger.info("Created {}", image);
            } catch (IOException | ImageException e) {
                logger.error("Failed to load and/or create HAL9000.svg", e);
            }
        };
    }

}
