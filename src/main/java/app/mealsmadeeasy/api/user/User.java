package app.mealsmadeeasy.api.user;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

public sealed interface User extends UserDetails permits UserEntity {

    Long getId();

    String getEmail();
    void setEmail(String email);

    void addAuthority(UserGrantedAuthority userGrantedAuthority);
    void addAuthorities(Set<? extends UserGrantedAuthority> userGrantedAuthorities);
    void removeAuthority(UserGrantedAuthority userGrantedAuthority);

}
