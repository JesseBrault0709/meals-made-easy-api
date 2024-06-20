package app.mealsmadeeasy.api.security;

import io.jsonwebtoken.Jwts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class KeyConfiguration {

    @Bean
    public SecretKey secretKey() {
        return Jwts.SIG.HS256.key().build();
    }

}
