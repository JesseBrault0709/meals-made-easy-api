package app.mealsmadeeasy.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public final class JwtServiceImpl implements JwtService {

    private final ObjectMapper objectMapper;
    private final long tokenLifetime;
    private final SecretKey secretKey;

    public JwtServiceImpl(
            ObjectMapper objectMapper,
            @Value("${app.mealsmadeeasy.api.security.token-lifetime}") Long tokenLifetime,
            SecretKey secretKey
    ) {
        this.objectMapper = objectMapper;
        this.tokenLifetime = tokenLifetime;
        this.secretKey = secretKey;
    }

    @Override
    public String generateToken(String username) {
        final Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(Instant.now().plusSeconds(this.tokenLifetime)))
                .signWith(this.secretKey)
                .json(new JacksonSerializer<>(this.objectMapper))
                .compact();
    }

}
