package app.mealsmadeeasy.api.jwt;

import app.mealsmadeeasy.api.security.AuthToken;
import io.jsonwebtoken.JwtException;

public interface JwtService {
    AuthToken generateAccessToken(String username);
    String getSubject(String token) throws JwtException;
}
