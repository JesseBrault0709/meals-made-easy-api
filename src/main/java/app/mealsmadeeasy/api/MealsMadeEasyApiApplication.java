package app.mealsmadeeasy.api;

import io.jsonwebtoken.Jwts;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import javax.crypto.SecretKey;

@SpringBootApplication
public class MealsMadeEasyApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MealsMadeEasyApiApplication.class, args);
	}

	@Bean
	public SecretKey secretKey() {
		return Jwts.SIG.HS256.key().build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		@SuppressWarnings("deprecation")
		UserDetails testUser = User
				.withDefaultPasswordEncoder()
				.username("test")
				.password("test")
				.roles("USER")
				.build();
		return new InMemoryUserDetailsManager(testUser);
	}

}
