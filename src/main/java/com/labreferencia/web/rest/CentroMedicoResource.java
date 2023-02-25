package com.labreferencia.web.rest;

import com.labreferencia.repository.CentroMedicoRepository;
import com.labreferencia.service.CentroMedicoService;
import com.labreferencia.service.dto.CentroMedicoDTO;
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
 * REST controller for managing {@link com.labreferencia.domain.CentroMedico}.
 */
@RestController
@RequestMapping("/api")
public class CentroMedicoResource {

    private final Logger log = LoggerFactory.getLogger(CentroMedicoResource.class);

    private static final String ENTITY_NAME = "centroMedico";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CentroMedicoService centroMedicoService;

    private final CentroMedicoRepository centroMedicoRepository;

    public CentroMedicoResource(CentroMedicoService centroMedicoService, CentroMedicoRepository centroMedicoRepository) {
        this.centroMedicoService = centroMedicoService;
        this.centroMedicoRepository = centroMedicoRepository;
    }

    /**
     * {@code POST  /centro-medicos} : Create a new centroMedico.
     *
     * @param centroMedicoDTO the centroMedicoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new centroMedicoDTO, or with status {@code 400 (Bad Request)} if the centroMedico has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/centro-medicos")
    public Mono<ResponseEntity<CentroMedicoDTO>> createCentroMedico(@Valid @RequestBody CentroMedicoDTO centroMedicoDTO)
        throws URISyntaxException {
        log.debug("REST request to save CentroMedico : {}", centroMedicoDTO);
        if (centroMedicoDTO.getId() != null) {
            throw new BadRequestAlertException("A new centroMedico cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return centroMedicoService
            .save(centroMedicoDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/centro-medicos/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /centro-medicos/:id} : Updates an existing centroMedico.
     *
     * @param id the id of the centroMedicoDTO to save.
     * @param centroMedicoDTO the centroMedicoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated centroMedicoDTO,
     * or with status {@code 400 (Bad Request)} if the centroMedicoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the centroMedicoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/centro-medicos/{id}")
    public Mono<ResponseEntity<CentroMedicoDTO>> updateCentroMedico(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CentroMedicoDTO centroMedicoDTO
    ) throws URISyntaxException {
        log.debug("REST request to update CentroMedico : {}, {}", id, centroMedicoDTO);
        if (centroMedicoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, centroMedicoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return centroMedicoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return centroMedicoService
                    .update(centroMedicoDTO)
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
     * {@code PATCH  /centro-medicos/:id} : Partial updates given fields of an existing centroMedico, field will ignore if it is null
     *
     * @param id the id of the centroMedicoDTO to save.
     * @param centroMedicoDTO the centroMedicoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated centroMedicoDTO,
     * or with status {@code 400 (Bad Request)} if the centroMedicoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the centroMedicoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the centroMedicoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/centro-medicos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<CentroMedicoDTO>> partialUpdateCentroMedico(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CentroMedicoDTO centroMedicoDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update CentroMedico partially : {}, {}", id, centroMedicoDTO);
        if (centroMedicoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, centroMedicoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return centroMedicoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<CentroMedicoDTO> result = centroMedicoService.partialUpdate(centroMedicoDTO);

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
     * {@code GET  /centro-medicos} : get all the centroMedicos.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of centroMedicos in body.
     */
    @GetMapping("/centro-medicos")
    public Mono<ResponseEntity<List<CentroMedicoDTO>>> getAllCentroMedicos(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of CentroMedicos");
        return centroMedicoService
            .countAll()
            .zipWith(centroMedicoService.findAll(pageable).collectList())
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
     * {@code GET  /centro-medicos/:id} : get the "id" centroMedico.
     *
     * @param id the id of the centroMedicoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the centroMedicoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/centro-medicos/{id}")
    public Mono<ResponseEntity<CentroMedicoDTO>> getCentroMedico(@PathVariable Long id) {
        log.debug("REST request to get CentroMedico : {}", id);
        Mono<CentroMedicoDTO> centroMedicoDTO = centroMedicoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(centroMedicoDTO);
    }

    /**
     * {@code DELETE  /centro-medicos/:id} : delete the "id" centroMedico.
     *
     * @param id the id of the centroMedicoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/centro-medicos/{id}")
    public Mono<ResponseEntity<Void>> deleteCentroMedico(@PathVariable Long id) {
        log.debug("REST request to delete CentroMedico : {}", id);
        return centroMedicoService
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
     * {@code SEARCH  /_search/centro-medicos?query=:query} : search for the centroMedico corresponding
     * to the query.
     *
     * @param query the query of the centroMedico search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/centro-medicos")
    public Mono<ResponseEntity<Flux<CentroMedicoDTO>>> searchCentroMedicos(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of CentroMedicos for query {}", query);
        return centroMedicoService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(centroMedicoService.search(query, pageable)));
    }
}
