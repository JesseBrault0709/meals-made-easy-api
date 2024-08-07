package app.mealsmadeeasy.api.jwt;

import app.mealsmadeeasy.api.security.AuthToken;
import app.mealsmadeeasy.api.security.SimpleAuthToken;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Serializer;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Service
public final class JwtServiceImpl implements JwtService {

    private final Serializer<Map<String, ?>> serializer;
    private final long accessTokenLifetime;
    private final SecretKey secretKey;

    public JwtServiceImpl(
            @Value("${app.mealsmadeeasy.api.security.access-token-lifetime}") Long accessTokenLifetime,
            SecretKey secretKey
    ) {
        this.serializer = new JacksonSerializer<>();
        this.accessTokenLifetime = accessTokenLifetime;
        this.secretKey = secretKey;
    }

    @Override
    public AuthToken generateAccessToken(String username) {
        final Instant now = Instant.now();
        final Instant expires = Instant.from(now.plusSeconds(accessTokenLifetime));
        final String token = Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expires))
                .signWith(this.secretKey)
                .json(this.serializer)
                .compact();
        return new SimpleAuthToken(
                token,
                this.accessTokenLifetime,
                LocalDateTime.ofInstant(expires, ZoneId.systemDefault())
        );
    }

    @Override
    public String getSubject(String token) throws JwtException {
        final var jws = Jwts.parser()
                .verifyWith(this.secretKey)
                .build()
                .parseSignedClaims(token);
        return jws.getPayload().getSubject();
    }

}
