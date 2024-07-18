package app.mealsmadeeasy.api;

import app.mealsmadeeasy.api.recipe.Recipe;
import app.mealsmadeeasy.api.recipe.RecipeService;
import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Set;

@SpringBootApplication
public class MealsMadeEasyApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MealsMadeEasyApiApplication.class, args);
	}

	private final UserService userService;
	private final RecipeService recipeService;

	public MealsMadeEasyApiApplication(UserService userService, RecipeService recipeService) {
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
