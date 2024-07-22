package app.mealsmadeeasy.api.image;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {

    @Query("SELECT image FROM Image image WHERE image.id = ?1")
    @EntityGraph(attributePaths = { "viewers" })
    ImageEntity getByIdWithViewers(long id);
    
}
