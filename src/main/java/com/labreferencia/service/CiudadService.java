package com.labreferencia.service;

import com.labreferencia.service.dto.CiudadDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.labreferencia.domain.Ciudad}.
 */
public interface CiudadService {
    /**
     * Save a ciudad.
     *
     * @param ciudadDTO the entity to save.
     * @return the persisted entity.
     */
    CiudadDTO save(CiudadDTO ciudadDTO);

    /**
     * Updates a ciudad.
     *
     * @param ciudadDTO the entity to update.
     * @return the persisted entity.
     */
    CiudadDTO update(CiudadDTO ciudadDTO);

    /**
     * Partially updates a ciudad.
     *
     * @param ciudadDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CiudadDTO> partialUpdate(CiudadDTO ciudadDTO);

    /**
     * Get all the ciudads.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CiudadDTO> findAll(Pageable pageable);

    /**
     * Get all the ciudads by nombre.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CiudadDTO> findAllByNombreContains(String query, Pageable pageable);

    /**
     * Get the "id" ciudad.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CiudadDTO> findOne(Long id);

    /**
     * Delete the "id" ciudad.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
