package ch.joeakeem.myconotes.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import ch.joeakeem.myconotes.domain.Image;
import ch.joeakeem.myconotes.repository.ImageRepository;
import ch.joeakeem.myconotes.repository.search.ImageSearchRepository;
import ch.joeakeem.myconotes.service.ImageService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Image}.
 */
@Service
@Transactional
public class ImageServiceImpl implements ImageService {

    private final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);

    private final ImageRepository imageRepository;

    private final ImageSearchRepository imageSearchRepository;

    public ImageServiceImpl(ImageRepository imageRepository, ImageSearchRepository imageSearchRepository) {
        this.imageRepository = imageRepository;
        this.imageSearchRepository = imageSearchRepository;
    }

    @Override
    public Image save(Image image) {
        log.debug("Request to save Image : {}", image);
        Image result = imageRepository.save(image);
        imageSearchRepository.index(result);
        return result;
    }

    @Override
    public Image update(Image image) {
        log.debug("Request to update Image : {}", image);
        Image result = imageRepository.save(image);
        imageSearchRepository.index(result);
        return result;
    }

    @Override
    public Optional<Image> partialUpdate(Image image) {
        log.debug("Request to partially update Image : {}", image);

        return imageRepository
            .findById(image.getId())
            .map(existingImage -> {
                if (image.getTitle() != null) {
                    existingImage.setTitle(image.getTitle());
                }
                if (image.getDescription() != null) {
                    existingImage.setDescription(image.getDescription());
                }
                if (image.getImage() != null) {
                    existingImage.setImage(image.getImage());
                }
                if (image.getImageContentType() != null) {
                    existingImage.setImageContentType(image.getImageContentType());
                }

                return existingImage;
            })
            .map(imageRepository::save)
            .map(savedImage -> {
                imageSearchRepository.save(savedImage);

                return savedImage;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Image> findAll(Pageable pageable) {
        log.debug("Request to get all Images");
        return imageRepository.findAll(pageable);
    }

    public Page<Image> findAllWithEagerRelationships(Pageable pageable) {
        return imageRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Image> findOne(Long id) {
        log.debug("Request to get Image : {}", id);
        return imageRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Image : {}", id);
        imageRepository.deleteById(id);
        imageSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Image> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Images for query {}", query);
        return imageSearchRepository.search(query, pageable);
    }
}
