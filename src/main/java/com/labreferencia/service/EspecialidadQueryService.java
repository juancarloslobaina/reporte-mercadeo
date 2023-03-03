package com.labreferencia.service;

import com.labreferencia.domain.*; // for static metamodels
import com.labreferencia.domain.Especialidad;
import com.labreferencia.repository.EspecialidadRepository;
import com.labreferencia.service.criteria.EspecialidadCriteria;
import com.labreferencia.service.dto.EspecialidadDTO;
import com.labreferencia.service.mapper.EspecialidadMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Especialidad} entities in the database.
 * The main input is a {@link EspecialidadCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link EspecialidadDTO} or a {@link Page} of {@link EspecialidadDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EspecialidadQueryService extends QueryService<Especialidad> {

    private final Logger log = LoggerFactory.getLogger(EspecialidadQueryService.class);

    private final EspecialidadRepository especialidadRepository;

    private final EspecialidadMapper especialidadMapper;

    public EspecialidadQueryService(EspecialidadRepository especialidadRepository, EspecialidadMapper especialidadMapper) {
        this.especialidadRepository = especialidadRepository;
        this.especialidadMapper = especialidadMapper;
    }

    /**
     * Return a {@link List} of {@link EspecialidadDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<EspecialidadDTO> findByCriteria(EspecialidadCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Especialidad> specification = createSpecification(criteria);
        return especialidadMapper.toDto(especialidadRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link EspecialidadDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<EspecialidadDTO> findByCriteria(EspecialidadCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Especialidad> specification = createSpecification(criteria);
        return especialidadRepository.findAll(specification, page).map(especialidadMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EspecialidadCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Especialidad> specification = createSpecification(criteria);
        return especialidadRepository.count(specification);
    }

    /**
     * Function to convert {@link EspecialidadCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Especialidad> createSpecification(EspecialidadCriteria criteria) {
        Specification<Especialidad> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Especialidad_.id));
            }
            if (criteria.getDescripcion() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescripcion(), Especialidad_.descripcion));
            }
        }
        return specification;
    }
}
