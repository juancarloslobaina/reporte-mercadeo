package com.labreferencia.reportemercadeo.service;

import com.labreferencia.reportemercadeo.service.dto.ReporteDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.labreferencia.reportemercadeo.domain.Reporte}.
 */
public interface ReporteService {
    /**
     * Save a reporte.
     *
     * @param reporteDTO the entity to save.
     * @return the persisted entity.
     */
    ReporteDTO save(ReporteDTO reporteDTO);

    /**
     * Updates a reporte.
     *
     * @param reporteDTO the entity to update.
     * @return the persisted entity.
     */
    ReporteDTO update(ReporteDTO reporteDTO);

    /**
     * Partially updates a reporte.
     *
     * @param reporteDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ReporteDTO> partialUpdate(ReporteDTO reporteDTO);

    /**
     * Get all the reportes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ReporteDTO> findAll(Pageable pageable);

    /**
     * Get all the reportes with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ReporteDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" reporte.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ReporteDTO> findOne(Long id);

    /**
     * Delete the "id" reporte.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
