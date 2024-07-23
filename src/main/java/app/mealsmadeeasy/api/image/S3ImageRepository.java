package app.mealsmadeeasy.api.image;

import app.mealsmadeeasy.api.user.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface S3ImageRepository extends JpaRepository<S3ImageEntity, Long> {

    @Query("SELECT image FROM Image image WHERE image.id = ?1")
    @EntityGraph(attributePaths = { "viewers" })
    S3ImageEntity getByIdWithViewers(long id);

    List<S3ImageEntity> findAllByOwner(UserEntity owner);
    Optional<S3ImageEntity> findByOwnerAndUserFilename(UserEntity owner, String filename);

}
