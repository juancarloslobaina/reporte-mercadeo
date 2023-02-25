package com.labreferencia.web.rest;

import com.labreferencia.repository.EspecialidadRepository;
import com.labreferencia.service.EspecialidadService;
import com.labreferencia.service.dto.EspecialidadDTO;
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
 * REST controller for managing {@link com.labreferencia.domain.Especialidad}.
 */
@RestController
@RequestMapping("/api")
public class EspecialidadResource {

    private final Logger log = LoggerFactory.getLogger(EspecialidadResource.class);

    private static final String ENTITY_NAME = "especialidad";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EspecialidadService especialidadService;

    private final EspecialidadRepository especialidadRepository;

    public EspecialidadResource(EspecialidadService especialidadService, EspecialidadRepository especialidadRepository) {
        this.especialidadService = especialidadService;
        this.especialidadRepository = especialidadRepository;
    }

    /**
     * {@code POST  /especialidads} : Create a new especialidad.
     *
     * @param especialidadDTO the especialidadDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new especialidadDTO, or with status {@code 400 (Bad Request)} if the especialidad has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/especialidads")
    public Mono<ResponseEntity<EspecialidadDTO>> createEspecialidad(@Valid @RequestBody EspecialidadDTO especialidadDTO)
        throws URISyntaxException {
        log.debug("REST request to save Especialidad : {}", especialidadDTO);
        if (especialidadDTO.getId() != null) {
            throw new BadRequestAlertException("A new especialidad cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return especialidadService
            .save(especialidadDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/especialidads/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /especialidads/:id} : Updates an existing especialidad.
     *
     * @param id the id of the especialidadDTO to save.
     * @param especialidadDTO the especialidadDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated especialidadDTO,
     * or with status {@code 400 (Bad Request)} if the especialidadDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the especialidadDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/especialidads/{id}")
    public Mono<ResponseEntity<EspecialidadDTO>> updateEspecialidad(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EspecialidadDTO especialidadDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Especialidad : {}, {}", id, especialidadDTO);
        if (especialidadDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, especialidadDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return especialidadRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return especialidadService
                    .update(especialidadDTO)
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
     * {@code PATCH  /especialidads/:id} : Partial updates given fields of an existing especialidad, field will ignore if it is null
     *
     * @param id the id of the especialidadDTO to save.
     * @param especialidadDTO the especialidadDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated especialidadDTO,
     * or with status {@code 400 (Bad Request)} if the especialidadDTO is not valid,
     * or with status {@code 404 (Not Found)} if the especialidadDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the especialidadDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/especialidads/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<EspecialidadDTO>> partialUpdateEspecialidad(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EspecialidadDTO especialidadDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Especialidad partially : {}, {}", id, especialidadDTO);
        if (especialidadDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, especialidadDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return especialidadRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<EspecialidadDTO> result = especialidadService.partialUpdate(especialidadDTO);

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
     * {@code GET  /especialidads} : get all the especialidads.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of especialidads in body.
     */
    @GetMapping("/especialidads")
    public Mono<ResponseEntity<List<EspecialidadDTO>>> getAllEspecialidads(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Especialidads");
        return especialidadService
            .countAll()
            .zipWith(especialidadService.findAll(pageable).collectList())
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
     * {@code GET  /especialidads/:id} : get the "id" especialidad.
     *
     * @param id the id of the especialidadDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the especialidadDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/especialidads/{id}")
    public Mono<ResponseEntity<EspecialidadDTO>> getEspecialidad(@PathVariable Long id) {
        log.debug("REST request to get Especialidad : {}", id);
        Mono<EspecialidadDTO> especialidadDTO = especialidadService.findOne(id);
        return ResponseUtil.wrapOrNotFound(especialidadDTO);
    }

    /**
     * {@code DELETE  /especialidads/:id} : delete the "id" especialidad.
     *
     * @param id the id of the especialidadDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/especialidads/{id}")
    public Mono<ResponseEntity<Void>> deleteEspecialidad(@PathVariable Long id) {
        log.debug("REST request to delete Especialidad : {}", id);
        return especialidadService
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
     * {@code SEARCH  /_search/especialidads?query=:query} : search for the especialidad corresponding
     * to the query.
     *
     * @param query the query of the especialidad search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/especialidads")
    public Mono<ResponseEntity<Flux<EspecialidadDTO>>> searchEspecialidads(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of Especialidads for query {}", query);
        return especialidadService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(especialidadService.search(query, pageable)));
    }
}
