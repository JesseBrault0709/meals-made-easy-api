package app.mealsmadeeasy.api.auth;

import app.mealsmadeeasy.api.user.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity(name = "RefreshToken")
public class RefreshTokenEntity implements RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;

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

    @Column(nullable = false)
    private Boolean deleted = false;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
    public LocalDateTime getExpires() {
        return this.expiration;
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }

    @Override
    public boolean isRevoked() {
        return this.revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public UserEntity getOwner() {
        return this.owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    @Override
    public boolean isDeleted() {
        return this.deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public long getLifetime() {
        return ChronoUnit.SECONDS.between(this.issued, this.expiration);
    }

}
