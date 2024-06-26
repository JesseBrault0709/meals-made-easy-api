package app.mealsmadeeasy.api.user;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

public interface User extends UserDetails {

    Long getId();

    String getEmail();
    void setEmail(String email);

    void addAuthority(UserGrantedAuthority userGrantedAuthority);
    void addAuthorities(Set<? extends UserGrantedAuthority> userGrantedAuthorities);
    void removeAuthority(UserGrantedAuthority userGrantedAuthority);

}
