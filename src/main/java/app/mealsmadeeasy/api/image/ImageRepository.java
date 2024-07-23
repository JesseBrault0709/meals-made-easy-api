package app.mealsmadeeasy.api.image;

import app.mealsmadeeasy.api.user.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {

    @Query("SELECT image FROM Image image WHERE image.id = ?1")
    @EntityGraph(attributePaths = { "viewers" })
    ImageEntity getByIdWithViewers(long id);

    List<ImageEntity> findAllByOwner(UserEntity owner);

}
