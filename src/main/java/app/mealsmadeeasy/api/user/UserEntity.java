package app.mealsmadeeasy.api.user;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = "User")
@Table(name = "\"user\"")
public final class UserEntity implements User {

    public static UserEntity getDefaultDraft() {
        final var user = new UserEntity();
        user.setEnabled(true);
        user.setExpired(false);
        user.setLocked(false);
        user.setCredentialsExpired(false);
        return user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "userEntity")
    private final Set<UserGrantedAuthorityEntity> authorities = new HashSet<>();

    @Column(nullable = false)
    private Boolean enabled;

    @Column(nullable = false)
    private Boolean expired;

    @Column(nullable = false)
    private Boolean locked;

    @Column(nullable = false)
    private Boolean credentialsExpired;

    @Override
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public void addAuthority(UserGrantedAuthority userGrantedAuthority) {
        this.authorities.add((UserGrantedAuthorityEntity) userGrantedAuthority);
    }

    @Override
    public void addAuthorities(Set<? extends UserGrantedAuthority> userGrantedAuthorities) {
        userGrantedAuthorities.forEach(this::addAuthority);
    }

    @Override
    public void removeAuthority(UserGrantedAuthority userGrantedAuthority) {
        this.authorities.remove((UserGrantedAuthorityEntity) userGrantedAuthority);
    }

    @Override
    public boolean isAccountNonExpired() {
        return !this.expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !this.credentialsExpired;
    }

    public void setCredentialsExpired(Boolean credentialsExpired) {
        this.credentialsExpired = credentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.id,
                this.username,
                this.email,
                this.password,
                this.authorities,
                this.enabled,
                this.expired,
                this.locked,
                this.credentialsExpired
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof User o) {
            return Objects.equals(this.id, o.getId())
                    && Objects.equals(this.username, o.getUsername())
                    && Objects.equals(this.password, o.getPassword())
                    && Objects.equals(this.authorities, o.getAuthorities())
                    && Objects.equals(this.enabled, o.isEnabled())
                    && Objects.equals(this.expired, !o.isAccountNonExpired())
                    && Objects.equals(this.locked, !o.isAccountNonLocked())
                    && Objects.equals(this.credentialsExpired, !o.isCredentialsNonExpired());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "UserEntity(" + this.id + ", " + this.username + ", " + this.email + ")";
    }

}
