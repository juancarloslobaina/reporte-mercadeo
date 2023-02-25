package com.labreferencia.service;

import com.labreferencia.service.dto.CentroMedicoDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.labreferencia.domain.CentroMedico}.
 */
public interface CentroMedicoService {
    /**
     * Save a centroMedico.
     *
     * @param centroMedicoDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<CentroMedicoDTO> save(CentroMedicoDTO centroMedicoDTO);

    /**
     * Updates a centroMedico.
     *
     * @param centroMedicoDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<CentroMedicoDTO> update(CentroMedicoDTO centroMedicoDTO);

    /**
     * Partially updates a centroMedico.
     *
     * @param centroMedicoDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<CentroMedicoDTO> partialUpdate(CentroMedicoDTO centroMedicoDTO);

    /**
     * Get all the centroMedicos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<CentroMedicoDTO> findAll(Pageable pageable);

    /**
     * Get all the centroMedicos with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<CentroMedicoDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of centroMedicos available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of centroMedicos available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" centroMedico.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<CentroMedicoDTO> findOne(Long id);

    /**
     * Delete the "id" centroMedico.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the centroMedico corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<CentroMedicoDTO> search(String query, Pageable pageable);
}
