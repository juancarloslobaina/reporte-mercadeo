package com.labreferencia.reportemercadeo.service;

import com.labreferencia.reportemercadeo.service.dto.CentroMedicoDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.labreferencia.reportemercadeo.domain.CentroMedico}.
 */
public interface CentroMedicoService {
    /**
     * Save a centroMedico.
     *
     * @param centroMedicoDTO the entity to save.
     * @return the persisted entity.
     */
    CentroMedicoDTO save(CentroMedicoDTO centroMedicoDTO);

    /**
     * Updates a centroMedico.
     *
     * @param centroMedicoDTO the entity to update.
     * @return the persisted entity.
     */
    CentroMedicoDTO update(CentroMedicoDTO centroMedicoDTO);

    /**
     * Partially updates a centroMedico.
     *
     * @param centroMedicoDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CentroMedicoDTO> partialUpdate(CentroMedicoDTO centroMedicoDTO);

    /**
     * Get all the centroMedicos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CentroMedicoDTO> findAll(Pageable pageable);

    /**
     * Get all the centroMedicos with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CentroMedicoDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" centroMedico.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CentroMedicoDTO> findOne(Long id);

    /**
     * Delete the "id" centroMedico.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
