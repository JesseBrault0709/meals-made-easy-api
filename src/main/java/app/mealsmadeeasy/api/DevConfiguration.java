package app.mealsmadeeasy.api;

import app.mealsmadeeasy.api.recipe.Recipe;
import app.mealsmadeeasy.api.recipe.RecipeService;
import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Set;

@Configuration
@Profile("dev")
public class DevConfiguration {

    private final UserService userService;
    private final RecipeService recipeService;

    public DevConfiguration(UserService userService, RecipeService recipeService) {
        this.userService = userService;
        this.recipeService = recipeService;
    }

    @Bean
    public CommandLineRunner addTestData() {
        return args -> {
            final User testUser = this.userService.createUser(
                    "test", "test@test.com", "test", Set.of()
            );
            final Recipe recipe = this.recipeService.create(testUser, "Test Recipe", "Hello, World!");
            this.recipeService.setPublic(recipe, testUser, true);
            System.out.println("Created " + testUser);
            System.out.println("Created " + recipe);
        };
    }

}
