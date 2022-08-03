package com.labreferencia.reportemercadeo.service;

import com.labreferencia.reportemercadeo.service.dto.CiudadDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.labreferencia.reportemercadeo.domain.Ciudad}.
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
