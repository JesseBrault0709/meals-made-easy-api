package app.mealsmadeeasy.api.auth;

import app.mealsmadeeasy.api.user.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity(name = "RefreshToken")
public class RefreshTokenEntity implements RefreshToken {

    @Id
    @Column(unique = true, nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime issued;

    @Column(nullable = false)
    private LocalDateTime expiration;

    @Column(nullable = false)
    private Boolean revoked = false;

    @JoinColumn(nullable = false)
    @ManyToOne
    private UserEntity owner;

    @Override
    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public LocalDateTime getIssued() {
        return this.issued;
    }

    public void setIssued(LocalDateTime issued) {
        this.issued = issued;
    }

    @Override
    public LocalDateTime getExpiration() {
        return this.expiration;
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }

    @Override
    public boolean isRevoked() {
        return this.revoked;
    }

    public void setRevoked(Boolean revoked) {
        this.revoked = revoked;
    }

    public UserEntity getOwner() {
        return this.owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    @Override
    public long getLifetime() {
        return ChronoUnit.SECONDS.between(this.issued, this.expiration);
    }

}
