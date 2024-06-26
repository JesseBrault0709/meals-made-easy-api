package app.mealsmadeeasy.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Serializer;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public final class JwtServiceImpl implements JwtService {

    private final Serializer<Map<String, ?>> serializer;
    private final long accessTokenLifetime;
    private final long refreshTokenLifetime;
    private final SecretKey secretKey;

    public JwtServiceImpl(
            ObjectMapper objectMapper,
            @Value("${app.mealsmadeeasy.api.security.access-token-lifetime}") Long accessTokenLifetime,
            @Value("${app.mealsmadeeasy.api.security.refresh-token-lifetime}") Long refreshTokenLifetime,
            SecretKey secretKey
    ) {
        this.serializer = new JacksonSerializer<>();
        this.accessTokenLifetime = accessTokenLifetime;
        this.refreshTokenLifetime = refreshTokenLifetime;
        this.secretKey = secretKey;
    }

    @Override
    public AuthToken generateAccessToken(String username) {
        final Instant now = Instant.now();
        final String token = Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(this.accessTokenLifetime)))
                .signWith(this.secretKey)
                .json(this.serializer)
                .compact();
        return new AuthToken(token, this.accessTokenLifetime);
    }

    @Override
    public AuthToken generateRefreshToken(String username) {
        final Instant now = Instant.now();
        final String token = Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(this.refreshTokenLifetime)))
                .signWith(this.secretKey)
                .json(this.serializer)
                .compact();
        return new AuthToken(token, this.refreshTokenLifetime);
    }

}
