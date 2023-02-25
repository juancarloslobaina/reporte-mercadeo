package com.labreferencia.web.rest;

import com.labreferencia.repository.ReporteRepository;
import com.labreferencia.service.ReporteService;
import com.labreferencia.service.dto.ReporteDTO;
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
 * REST controller for managing {@link com.labreferencia.domain.Reporte}.
 */
@RestController
@RequestMapping("/api")
public class ReporteResource {

    private final Logger log = LoggerFactory.getLogger(ReporteResource.class);

    private static final String ENTITY_NAME = "reporte";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReporteService reporteService;

    private final ReporteRepository reporteRepository;

    public ReporteResource(ReporteService reporteService, ReporteRepository reporteRepository) {
        this.reporteService = reporteService;
        this.reporteRepository = reporteRepository;
    }

    /**
     * {@code POST  /reportes} : Create a new reporte.
     *
     * @param reporteDTO the reporteDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new reporteDTO, or with status {@code 400 (Bad Request)} if the reporte has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/reportes")
    public Mono<ResponseEntity<ReporteDTO>> createReporte(@Valid @RequestBody ReporteDTO reporteDTO) throws URISyntaxException {
        log.debug("REST request to save Reporte : {}", reporteDTO);
        if (reporteDTO.getId() != null) {
            throw new BadRequestAlertException("A new reporte cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return reporteService
            .save(reporteDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/reportes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /reportes/:id} : Updates an existing reporte.
     *
     * @param id the id of the reporteDTO to save.
     * @param reporteDTO the reporteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reporteDTO,
     * or with status {@code 400 (Bad Request)} if the reporteDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the reporteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/reportes/{id}")
    public Mono<ResponseEntity<ReporteDTO>> updateReporte(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ReporteDTO reporteDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Reporte : {}, {}", id, reporteDTO);
        if (reporteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reporteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return reporteRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return reporteService
                    .update(reporteDTO)
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
     * {@code PATCH  /reportes/:id} : Partial updates given fields of an existing reporte, field will ignore if it is null
     *
     * @param id the id of the reporteDTO to save.
     * @param reporteDTO the reporteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reporteDTO,
     * or with status {@code 400 (Bad Request)} if the reporteDTO is not valid,
     * or with status {@code 404 (Not Found)} if the reporteDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the reporteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/reportes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ReporteDTO>> partialUpdateReporte(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ReporteDTO reporteDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Reporte partially : {}, {}", id, reporteDTO);
        if (reporteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reporteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return reporteRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ReporteDTO> result = reporteService.partialUpdate(reporteDTO);

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
     * {@code GET  /reportes} : get all the reportes.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of reportes in body.
     */
    @GetMapping("/reportes")
    public Mono<ResponseEntity<List<ReporteDTO>>> getAllReportes(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Reportes");
        return reporteService
            .countAll()
            .zipWith(reporteService.findAll(pageable).collectList())
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
     * {@code GET  /reportes/:id} : get the "id" reporte.
     *
     * @param id the id of the reporteDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the reporteDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/reportes/{id}")
    public Mono<ResponseEntity<ReporteDTO>> getReporte(@PathVariable Long id) {
        log.debug("REST request to get Reporte : {}", id);
        Mono<ReporteDTO> reporteDTO = reporteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(reporteDTO);
    }

    /**
     * {@code DELETE  /reportes/:id} : delete the "id" reporte.
     *
     * @param id the id of the reporteDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/reportes/{id}")
    public Mono<ResponseEntity<Void>> deleteReporte(@PathVariable Long id) {
        log.debug("REST request to delete Reporte : {}", id);
        return reporteService
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
     * {@code SEARCH  /_search/reportes?query=:query} : search for the reporte corresponding
     * to the query.
     *
     * @param query the query of the reporte search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/reportes")
    public Mono<ResponseEntity<Flux<ReporteDTO>>> searchReportes(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of Reportes for query {}", query);
        return reporteService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(reporteService.search(query, pageable)));
    }
}
