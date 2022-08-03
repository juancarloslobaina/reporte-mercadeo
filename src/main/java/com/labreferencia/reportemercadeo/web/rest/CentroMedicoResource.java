package com.labreferencia.reportemercadeo.web.rest;

import com.labreferencia.reportemercadeo.repository.CentroMedicoRepository;
import com.labreferencia.reportemercadeo.service.CentroMedicoService;
import com.labreferencia.reportemercadeo.service.dto.CentroMedicoDTO;
import com.labreferencia.reportemercadeo.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.labreferencia.reportemercadeo.domain.CentroMedico}.
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
    public ResponseEntity<CentroMedicoDTO> createCentroMedico(@Valid @RequestBody CentroMedicoDTO centroMedicoDTO)
        throws URISyntaxException {
        log.debug("REST request to save CentroMedico : {}", centroMedicoDTO);
        if (centroMedicoDTO.getId() != null) {
            throw new BadRequestAlertException("A new centroMedico cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CentroMedicoDTO result = centroMedicoService.save(centroMedicoDTO);
        return ResponseEntity
            .created(new URI("/api/centro-medicos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
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
    public ResponseEntity<CentroMedicoDTO> updateCentroMedico(
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

        if (!centroMedicoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CentroMedicoDTO result = centroMedicoService.update(centroMedicoDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, centroMedicoDTO.getId().toString()))
            .body(result);
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
    public ResponseEntity<CentroMedicoDTO> partialUpdateCentroMedico(
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

        if (!centroMedicoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CentroMedicoDTO> result = centroMedicoService.partialUpdate(centroMedicoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, centroMedicoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /centro-medicos} : get all the centroMedicos.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of centroMedicos in body.
     */
    @GetMapping("/centro-medicos")
    public ResponseEntity<List<CentroMedicoDTO>> getAllCentroMedicos(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of CentroMedicos");
        Page<CentroMedicoDTO> page;
        if (eagerload) {
            page = centroMedicoService.findAllWithEagerRelationships(pageable);
        } else {
            page = centroMedicoService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /centro-medicos/:id} : get the "id" centroMedico.
     *
     * @param id the id of the centroMedicoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the centroMedicoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/centro-medicos/{id}")
    public ResponseEntity<CentroMedicoDTO> getCentroMedico(@PathVariable Long id) {
        log.debug("REST request to get CentroMedico : {}", id);
        Optional<CentroMedicoDTO> centroMedicoDTO = centroMedicoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(centroMedicoDTO);
    }

    /**
     * {@code DELETE  /centro-medicos/:id} : delete the "id" centroMedico.
     *
     * @param id the id of the centroMedicoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/centro-medicos/{id}")
    public ResponseEntity<Void> deleteCentroMedico(@PathVariable Long id) {
        log.debug("REST request to delete CentroMedico : {}", id);
        centroMedicoService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
