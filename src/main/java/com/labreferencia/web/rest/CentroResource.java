package com.labreferencia.web.rest;

import com.labreferencia.repository.CentroRepository;
import com.labreferencia.security.AuthoritiesConstants;
import com.labreferencia.security.SecurityUtils;
import com.labreferencia.service.CentroQueryService;
import com.labreferencia.service.CentroService;
import com.labreferencia.service.UserService;
import com.labreferencia.service.criteria.CentroCriteria;
import com.labreferencia.service.dto.CentroDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.labreferencia.domain.Centro}.
 */
@RestController
@RequestMapping("/api")
public class CentroResource {

    private final Logger log = LoggerFactory.getLogger(CentroResource.class);

    private static final String ENTITY_NAME = "centro";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CentroService centroService;

    private final CentroRepository centroRepository;

    private final CentroQueryService centroQueryService;

    private final UserService userService;

    public CentroResource(
        CentroService centroService,
        CentroRepository centroRepository,
        CentroQueryService centroQueryService,
        UserService userService
    ) {
        this.centroService = centroService;
        this.centroRepository = centroRepository;
        this.centroQueryService = centroQueryService;
        this.userService = userService;
    }

    /**
     * {@code POST  /centros} : Create a new centro.
     *
     * @param centroDTO the centroDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new centroDTO, or with status {@code 400 (Bad Request)} if the centro has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/centros")
    public ResponseEntity<CentroDTO> createCentro(@Valid @RequestBody CentroDTO centroDTO) throws URISyntaxException {
        log.debug("REST request to save Centro : {}", centroDTO);
        if (centroDTO.getId() != null) {
            throw new BadRequestAlertException("A new centro cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (userService.setUserByUserLogin(centroDTO)) return new ResponseEntity("error.http.403", HttpStatus.FORBIDDEN);

        CentroDTO result = centroService.save(centroDTO);
        return ResponseEntity
            .created(new URI("/api/centros/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /centros/:id} : Updates an existing centro.
     *
     * @param id the id of the centroDTO to save.
     * @param centroDTO the centroDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated centroDTO,
     * or with status {@code 400 (Bad Request)} if the centroDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the centroDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/centros/{id}")
    public ResponseEntity<CentroDTO> updateCentro(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CentroDTO centroDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Centro : {}, {}", id, centroDTO);
        if (validateDTOParameters(id, centroDTO)) return new ResponseEntity("error.http.403", HttpStatus.FORBIDDEN);

        CentroDTO result = centroService.update(centroDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, centroDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /centros/:id} : Partial updates given fields of an existing centro, field will ignore if it is null
     *
     * @param id the id of the centroDTO to save.
     * @param centroDTO the centroDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated centroDTO,
     * or with status {@code 400 (Bad Request)} if the centroDTO is not valid,
     * or with status {@code 404 (Not Found)} if the centroDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the centroDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/centros/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CentroDTO> partialUpdateCentro(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CentroDTO centroDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Centro partially : {}, {}", id, centroDTO);
        if (validateDTOParameters(id, centroDTO)) return new ResponseEntity("error.http.403", HttpStatus.FORBIDDEN);

        Optional<CentroDTO> result = centroService.partialUpdate(centroDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, centroDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /centros} : get all the centros.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of centros in body.
     */
    @GetMapping("/centros")
    public ResponseEntity<List<CentroDTO>> getAllCentros(
        CentroCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Centros by criteria: {}", criteria);
        Page<CentroDTO> page;
        if (SecurityUtils.hasCurrentUserNoneOfAuthorities(AuthoritiesConstants.ADMIN)) {
            page = centroService.findByUserLogin(SecurityUtils.getCurrentUserLogin().orElse(null), pageable);
        } else {
            page = centroQueryService.findByCriteria(criteria, pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /centros/count} : count all the centros.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/centros/count")
    public ResponseEntity<Long> countCentros(CentroCriteria criteria) {
        log.debug("REST request to count Centros by criteria: {}", criteria);
        return ResponseEntity.ok().body(centroQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /centros/:id} : get the "id" centro.
     *
     * @param id the id of the centroDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the centroDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/centros/{id}")
    public ResponseEntity<CentroDTO> getCentro(@PathVariable Long id) {
        log.debug("REST request to get Centro : {}", id);
        Optional<CentroDTO> centroDTO = centroService.findOne(id);
        if (userService.checkUserIsCurrentOrAdministrator(centroDTO.orElse(null))) return new ResponseEntity(
            "Unauthorized",
            HttpStatus.UNAUTHORIZED
        );
        return ResponseUtil.wrapOrNotFound(centroDTO);
    }

    /**
     * {@code DELETE  /centros/:id} : delete the "id" centro.
     *
     * @param id the id of the centroDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/centros/{id}")
    public ResponseEntity<Void> deleteCentro(@PathVariable Long id) {
        log.debug("REST request to delete Centro : {}", id);
        Optional<CentroDTO> centroDTO = centroService.findOne(id);
        if (userService.checkUserIsCurrentOrAdministrator(centroDTO.orElse(null))) return new ResponseEntity(
            "Unauthorized",
            HttpStatus.UNAUTHORIZED
        );
        centroService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    private boolean validateDTOParameters(Long id, CentroDTO centroDTO) {
        if (centroDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, centroDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!centroRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        return (userService.setUserByUserLogin(centroDTO));
    }
}
