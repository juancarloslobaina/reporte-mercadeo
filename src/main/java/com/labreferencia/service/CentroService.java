package com.labreferencia.service;

import com.labreferencia.service.dto.CentroDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.labreferencia.domain.Centro}.
 */
public interface CentroService {
    /**
     * Save a centro.
     *
     * @param centroDTO the entity to save.
     * @return the persisted entity.
     */
    CentroDTO save(CentroDTO centroDTO);

    /**
     * Updates a centro.
     *
     * @param centroDTO the entity to update.
     * @return the persisted entity.
     */
    CentroDTO update(CentroDTO centroDTO);

    /**
     * Partially updates a centro.
     *
     * @param centroDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CentroDTO> partialUpdate(CentroDTO centroDTO);

    /**
     * Get all the centros.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CentroDTO> findAll(Pageable pageable);

    /**
     * Get all the centros with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CentroDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" centro.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CentroDTO> findOne(Long id);

    /**
     * Delete the "id" centro.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
