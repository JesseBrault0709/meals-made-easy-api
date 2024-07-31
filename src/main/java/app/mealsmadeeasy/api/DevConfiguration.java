package app.mealsmadeeasy.api;

import app.mealsmadeeasy.api.image.Image;
import app.mealsmadeeasy.api.image.ImageService;
import app.mealsmadeeasy.api.image.spec.ImageCreateInfoSpec;
import app.mealsmadeeasy.api.recipe.Recipe;
import app.mealsmadeeasy.api.recipe.RecipeService;
import app.mealsmadeeasy.api.recipe.spec.RecipeCreateSpec;
import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

@Configuration
@Profile("dev")
public class DevConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DevConfiguration.class);

    private final UserService userService;
    private final RecipeService recipeService;
    private final ImageService imageService;

    private final ObjectMapper yamlObjectMapper = new ObjectMapper(new YAMLFactory());

    public DevConfiguration(UserService userService, RecipeService recipeService, ImageService imageService) {
        this.userService = userService;
        this.recipeService = recipeService;
        this.imageService = imageService;
    }

    private static final class ImageInfo {
        public String src;
        public String title;
        public String alt;
        public String caption;
        public boolean isPublic;
    }

    private static final class RecipeFrontMatter {
        public String title;
        public String slug;
        public boolean isPublic;
        public ImageInfo mainImage;
    }

    @Bean
    public CommandLineRunner addTestData() {
        return args -> {
            final User testUser = this.userService.createUser(
                    "test-user", "test-user@test.com", "test", Set.of()
            );
            logger.info("Created testUser: {}", testUser);

            try (final Stream<Path> recipePaths = Files.walk(Path.of("dev-data/recipes"))) {
                for (final Path recipePath : recipePaths.toList()) {
                    if (Files.isDirectory(recipePath)) {
                        continue;
                    }

                    final String fullText = Files.readString(recipePath);
                    final String[] parts = fullText.split("---");
                    if (parts.length != 3) {
                        throw new IllegalArgumentException("Invalid recipe file: " + recipePath);
                    }
                    final String rawFrontMatter = parts[1];
                    final String rawRecipeText = parts[2];
                    final RecipeFrontMatter frontMatter = this.yamlObjectMapper.readValue(
                            rawFrontMatter, RecipeFrontMatter.class
                    );

                    final ImageCreateInfoSpec imageCreateSpec = new ImageCreateInfoSpec();
                    imageCreateSpec.setAlt(frontMatter.mainImage.alt);
                    imageCreateSpec.setCaption(frontMatter.mainImage.caption);
                    imageCreateSpec.setPublic(frontMatter.mainImage.isPublic);
                    final Path givenPath = Path.of(frontMatter.mainImage.src);
                    final Path resolvedPath = Path.of("dev-data").resolve(givenPath);
                    final Image mainImage;
                    try (final InputStream inputStream = new FileInputStream(resolvedPath.toFile())) {
                        final long size = Files.size(resolvedPath);
                        mainImage = this.imageService.create(
                                testUser,
                                givenPath.getName(givenPath.getNameCount() - 1).toString(),
                                inputStream,
                                size,
                                imageCreateSpec
                        );
                        logger.info("Created mainImage {} for {}", mainImage, recipePath);
                    }

                    final RecipeCreateSpec recipeCreateSpec = new RecipeCreateSpec();
                    recipeCreateSpec.setSlug(frontMatter.slug);
                    recipeCreateSpec.setTitle(frontMatter.title);
                    recipeCreateSpec.setRawText(rawRecipeText);
                    recipeCreateSpec.setPublic(frontMatter.isPublic);
                    recipeCreateSpec.setMainImage(mainImage);
                    final Recipe recipe = this.recipeService.create(testUser, recipeCreateSpec);
                    logger.info("Created recipe {}", recipe);
                }
            }
        };
    }

}
