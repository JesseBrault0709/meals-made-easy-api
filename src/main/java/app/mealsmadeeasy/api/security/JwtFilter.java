package app.mealsmadeeasy.api.security;

import app.mealsmadeeasy.api.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Lazy
@Component
public final class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public JwtFilter(UserDetailsService userDetailsService, JwtService jwtService, ObjectMapper objectMapper) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    private void handleSecurityException(
            HttpServletResponse response,
            int status,
            SecurityExceptionView.Action action,
            String message
    ) throws IOException {
        final SecurityExceptionView view = new SecurityExceptionView(status, action, message);
        response.setStatus(status);
        response.getWriter().write(this.objectMapper.writeValueAsString(view));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (authorizationHeader.startsWith("Bearer ") && authorizationHeader.length() > 7) {
            final String token = authorizationHeader.substring(7);
            final String username;
            try {
                username = this.jwtService.getSubject(token);
            } catch (ExpiredJwtException expiredJwtException) {
                this.handleSecurityException(
                        response,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        SecurityExceptionView.Action.REFRESH,
                        expiredJwtException.getMessage()
                );
                return;
            } catch (JwtException jwtException) {
                logger.error("Error while getting username from token.", jwtException);
                this.handleSecurityException(
                        response,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        SecurityExceptionView.Action.REFRESH,
                        jwtException.getMessage()
                );
                return;
            }
            final UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            final var authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);
    }

}
