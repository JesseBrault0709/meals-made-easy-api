package app.mealsmadeeasy.api.user;

import jakarta.persistence.*;

@Entity
public final class UserGrantedAuthorityEntity implements UserGrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    private String authority;

    @ManyToOne
    @JoinColumn(name = "user_entity_id")
    private UserEntity userEntity;

    @Override
    public String getAuthority() {
        return this.authority;
    }

}
