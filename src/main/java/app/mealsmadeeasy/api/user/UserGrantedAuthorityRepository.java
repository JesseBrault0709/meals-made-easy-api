package app.mealsmadeeasy.api.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGrantedAuthorityRepository extends JpaRepository<UserGrantedAuthorityEntity, Long> {}
