package com.labreferencia.service;

import com.labreferencia.service.dto.ReporteDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.labreferencia.domain.Reporte}.
 */
public interface ReporteService {
    /**
     * Save a reporte.
     *
     * @param reporteDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ReporteDTO> save(ReporteDTO reporteDTO);

    /**
     * Updates a reporte.
     *
     * @param reporteDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ReporteDTO> update(ReporteDTO reporteDTO);

    /**
     * Partially updates a reporte.
     *
     * @param reporteDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ReporteDTO> partialUpdate(ReporteDTO reporteDTO);

    /**
     * Get all the reportes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ReporteDTO> findAll(Pageable pageable);

    /**
     * Get all the reportes with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ReporteDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of reportes available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of reportes available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" reporte.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ReporteDTO> findOne(Long id);

    /**
     * Delete the "id" reporte.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the reporte corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ReporteDTO> search(String query, Pageable pageable);
}
