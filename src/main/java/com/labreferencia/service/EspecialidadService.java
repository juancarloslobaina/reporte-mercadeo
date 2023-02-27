package com.labreferencia.service;

import com.labreferencia.service.dto.EspecialidadDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.labreferencia.domain.Especialidad}.
 */
public interface EspecialidadService {
    /**
     * Save a especialidad.
     *
     * @param especialidadDTO the entity to save.
     * @return the persisted entity.
     */
    EspecialidadDTO save(EspecialidadDTO especialidadDTO);

    /**
     * Updates a especialidad.
     *
     * @param especialidadDTO the entity to update.
     * @return the persisted entity.
     */
    EspecialidadDTO update(EspecialidadDTO especialidadDTO);

    /**
     * Partially updates a especialidad.
     *
     * @param especialidadDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<EspecialidadDTO> partialUpdate(EspecialidadDTO especialidadDTO);

    /**
     * Get all the especialidads.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<EspecialidadDTO> findAll(Pageable pageable);

    /**
     * Get the "id" especialidad.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EspecialidadDTO> findOne(Long id);

    /**
     * Delete the "id" especialidad.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
