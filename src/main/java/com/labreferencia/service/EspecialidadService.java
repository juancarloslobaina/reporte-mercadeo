package com.labreferencia.service;

import com.labreferencia.service.dto.EspecialidadDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    Mono<EspecialidadDTO> save(EspecialidadDTO especialidadDTO);

    /**
     * Updates a especialidad.
     *
     * @param especialidadDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<EspecialidadDTO> update(EspecialidadDTO especialidadDTO);

    /**
     * Partially updates a especialidad.
     *
     * @param especialidadDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<EspecialidadDTO> partialUpdate(EspecialidadDTO especialidadDTO);

    /**
     * Get all the especialidads.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<EspecialidadDTO> findAll(Pageable pageable);

    /**
     * Returns the number of especialidads available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of especialidads available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" especialidad.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<EspecialidadDTO> findOne(Long id);

    /**
     * Delete the "id" especialidad.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the especialidad corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<EspecialidadDTO> search(String query, Pageable pageable);
}
