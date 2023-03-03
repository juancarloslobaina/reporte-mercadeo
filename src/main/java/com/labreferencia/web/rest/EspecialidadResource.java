package com.labreferencia.web.rest;

import com.labreferencia.repository.EspecialidadRepository;
import com.labreferencia.service.EspecialidadQueryService;
import com.labreferencia.service.EspecialidadService;
import com.labreferencia.service.criteria.EspecialidadCriteria;
import com.labreferencia.service.dto.EspecialidadDTO;
import com.labreferencia.web.rest.errors.BadRequestAlertException;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

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

    private final EspecialidadQueryService especialidadQueryService;

    public EspecialidadResource(
        EspecialidadService especialidadService,
        EspecialidadRepository especialidadRepository,
        EspecialidadQueryService especialidadQueryService
    ) {
        this.especialidadService = especialidadService;
        this.especialidadRepository = especialidadRepository;
        this.especialidadQueryService = especialidadQueryService;
    }

    /**
     * {@code POST  /especialidads} : Create a new especialidad.
     *
     * @param especialidadDTO the especialidadDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new especialidadDTO, or with status {@code 400 (Bad Request)} if the especialidad has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/especialidads")
    public ResponseEntity<EspecialidadDTO> createEspecialidad(@Valid @RequestBody EspecialidadDTO especialidadDTO)
        throws URISyntaxException {
        log.debug("REST request to save Especialidad : {}", especialidadDTO);
        if (especialidadDTO.getId() != null) {
            throw new BadRequestAlertException("A new especialidad cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EspecialidadDTO result = especialidadService.save(especialidadDTO);
        return ResponseEntity
            .created(new URI("/api/especialidads/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
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
    public ResponseEntity<EspecialidadDTO> updateEspecialidad(
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

        if (!especialidadRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        EspecialidadDTO result = especialidadService.update(especialidadDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, especialidadDTO.getId().toString()))
            .body(result);
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
    public ResponseEntity<EspecialidadDTO> partialUpdateEspecialidad(
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

        if (!especialidadRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EspecialidadDTO> result = especialidadService.partialUpdate(especialidadDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, especialidadDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /especialidads} : get all the especialidads.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of especialidads in body.
     */
    @GetMapping("/especialidads")
    public ResponseEntity<List<EspecialidadDTO>> getAllEspecialidads(
        EspecialidadCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Especialidads by criteria: {}", criteria);
        Page<EspecialidadDTO> page = especialidadQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /especialidads/count} : count all the especialidads.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/especialidads/count")
    public ResponseEntity<Long> countEspecialidads(EspecialidadCriteria criteria) {
        log.debug("REST request to count Especialidads by criteria: {}", criteria);
        return ResponseEntity.ok().body(especialidadQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /especialidads/:id} : get the "id" especialidad.
     *
     * @param id the id of the especialidadDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the especialidadDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/especialidads/{id}")
    public ResponseEntity<EspecialidadDTO> getEspecialidad(@PathVariable Long id) {
        log.debug("REST request to get Especialidad : {}", id);
        Optional<EspecialidadDTO> especialidadDTO = especialidadService.findOne(id);
        return ResponseUtil.wrapOrNotFound(especialidadDTO);
    }

    /**
     * {@code DELETE  /especialidads/:id} : delete the "id" especialidad.
     *
     * @param id the id of the especialidadDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/especialidads/{id}")
    public ResponseEntity<Void> deleteEspecialidad(@PathVariable Long id) {
        log.debug("REST request to delete Especialidad : {}", id);
        especialidadService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
