package com.labreferencia.web.rest;

import com.labreferencia.repository.CiudadRepository;
import com.labreferencia.service.CiudadService;
import com.labreferencia.service.dto.CiudadDTO;
import com.labreferencia.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.labreferencia.domain.Ciudad}.
 */
@RestController
@RequestMapping("/api")
public class CiudadResource {

    private final Logger log = LoggerFactory.getLogger(CiudadResource.class);

    private static final String ENTITY_NAME = "ciudad";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CiudadService ciudadService;

    private final CiudadRepository ciudadRepository;

    public CiudadResource(CiudadService ciudadService, CiudadRepository ciudadRepository) {
        this.ciudadService = ciudadService;
        this.ciudadRepository = ciudadRepository;
    }

    /**
     * {@code POST  /ciudads} : Create a new ciudad.
     *
     * @param ciudadDTO the ciudadDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ciudadDTO, or with status {@code 400 (Bad Request)} if the ciudad has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ciudads")
    public Mono<ResponseEntity<CiudadDTO>> createCiudad(@Valid @RequestBody CiudadDTO ciudadDTO) throws URISyntaxException {
        log.debug("REST request to save Ciudad : {}", ciudadDTO);
        if (ciudadDTO.getId() != null) {
            throw new BadRequestAlertException("A new ciudad cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return ciudadService
            .save(ciudadDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/ciudads/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /ciudads/:id} : Updates an existing ciudad.
     *
     * @param id the id of the ciudadDTO to save.
     * @param ciudadDTO the ciudadDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ciudadDTO,
     * or with status {@code 400 (Bad Request)} if the ciudadDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ciudadDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/ciudads/{id}")
    public Mono<ResponseEntity<CiudadDTO>> updateCiudad(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CiudadDTO ciudadDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Ciudad : {}, {}", id, ciudadDTO);
        if (ciudadDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ciudadDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return ciudadRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return ciudadService
                    .update(ciudadDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /ciudads/:id} : Partial updates given fields of an existing ciudad, field will ignore if it is null
     *
     * @param id the id of the ciudadDTO to save.
     * @param ciudadDTO the ciudadDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ciudadDTO,
     * or with status {@code 400 (Bad Request)} if the ciudadDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ciudadDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ciudadDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/ciudads/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<CiudadDTO>> partialUpdateCiudad(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CiudadDTO ciudadDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Ciudad partially : {}, {}", id, ciudadDTO);
        if (ciudadDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ciudadDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return ciudadRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<CiudadDTO> result = ciudadService.partialUpdate(ciudadDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /ciudads} : get all the ciudads.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ciudads in body.
     */
    @GetMapping("/ciudads")
    public Mono<ResponseEntity<List<CiudadDTO>>> getAllCiudads(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Ciudads");
        return ciudadService
            .countAll()
            .zipWith(ciudadService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /ciudads/:id} : get the "id" ciudad.
     *
     * @param id the id of the ciudadDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ciudadDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/ciudads/{id}")
    public Mono<ResponseEntity<CiudadDTO>> getCiudad(@PathVariable Long id) {
        log.debug("REST request to get Ciudad : {}", id);
        Mono<CiudadDTO> ciudadDTO = ciudadService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ciudadDTO);
    }

    /**
     * {@code DELETE  /ciudads/:id} : delete the "id" ciudad.
     *
     * @param id the id of the ciudadDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/ciudads/{id}")
    public Mono<ResponseEntity<Void>> deleteCiudad(@PathVariable Long id) {
        log.debug("REST request to delete Ciudad : {}", id);
        return ciudadService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    /**
     * {@code SEARCH  /_search/ciudads?query=:query} : search for the ciudad corresponding
     * to the query.
     *
     * @param query the query of the ciudad search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/ciudads")
    public Mono<ResponseEntity<Flux<CiudadDTO>>> searchCiudads(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of Ciudads for query {}", query);
        return ciudadService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(ciudadService.search(query, pageable)));
    }
}
