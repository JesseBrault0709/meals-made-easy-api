package app.mealsmadeeasy.api;

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

	public MealsMadeEasyApiApplication(UserService userService) {
		this.userService = userService;
	}

	@Bean
	public CommandLineRunner addTestUser() {
		return args -> {
			this.userService.createUser("test", "test@test.com", "test", Set.of());
		};
	}

}
