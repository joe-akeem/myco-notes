package ch.joeakeem.myconotes.service;

import ch.joeakeem.myconotes.domain.Image;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Image}.
 */
public interface ImageService {
    /**
     * Save a image.
     *
     * @param image the entity to save.
     * @return the persisted entity.
     */
    Image save(Image image);

    /**
     * Updates a image.
     *
     * @param image the entity to update.
     * @return the persisted entity.
     */
    Image update(Image image);

    /**
     * Partially updates a image.
     *
     * @param image the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Image> partialUpdate(Image image);

    /**
     * Get all the images.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Image> findAll(Pageable pageable);

    /**
     * Get all the images with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Image> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" image.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Image> findOne(Long id);

    /**
     * Delete the "id" image.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the image corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Image> search(String query, Pageable pageable);
}
