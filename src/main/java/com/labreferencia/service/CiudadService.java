package com.labreferencia.service;

import com.labreferencia.service.dto.CiudadDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    Mono<CiudadDTO> save(CiudadDTO ciudadDTO);

    /**
     * Updates a ciudad.
     *
     * @param ciudadDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<CiudadDTO> update(CiudadDTO ciudadDTO);

    /**
     * Partially updates a ciudad.
     *
     * @param ciudadDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<CiudadDTO> partialUpdate(CiudadDTO ciudadDTO);

    /**
     * Get all the ciudads.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<CiudadDTO> findAll(Pageable pageable);

    /**
     * Returns the number of ciudads available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of ciudads available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" ciudad.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<CiudadDTO> findOne(Long id);

    /**
     * Delete the "id" ciudad.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Search for the ciudad corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<CiudadDTO> search(String query, Pageable pageable);
}
