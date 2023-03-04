package com.labreferencia.service;

import com.labreferencia.domain.Ciudad;
import com.labreferencia.domain.Ciudad_;
import com.labreferencia.repository.CiudadRepository;
import com.labreferencia.service.criteria.CiudadCriteria;
import com.labreferencia.service.dto.CiudadDTO;
import com.labreferencia.service.mapper.CiudadMapper;
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
 * Service for executing complex queries for {@link Ciudad} entities in the database.
 * The main input is a {@link CiudadCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CiudadDTO} or a {@link Page} of {@link CiudadDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CiudadQueryService extends QueryService<Ciudad> {

    private final Logger log = LoggerFactory.getLogger(CiudadQueryService.class);

    private final CiudadRepository ciudadRepository;

    private final CiudadMapper ciudadMapper;

    public CiudadQueryService(CiudadRepository ciudadRepository, CiudadMapper ciudadMapper) {
        this.ciudadRepository = ciudadRepository;
        this.ciudadMapper = ciudadMapper;
    }

    /**
     * Return a {@link List} of {@link CiudadDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CiudadDTO> findByCriteria(CiudadCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Ciudad> specification = createSpecification(criteria);
        return ciudadMapper.toDto(ciudadRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link CiudadDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CiudadDTO> findByCriteria(CiudadCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Ciudad> specification = createSpecification(criteria);
        return ciudadRepository.findAll(specification, page).map(ciudadMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CiudadCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Ciudad> specification = createSpecification(criteria);
        return ciudadRepository.count(specification);
    }

    /**
     * Function to convert {@link CiudadCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Ciudad> createSpecification(CiudadCriteria criteria) {
        Specification<Ciudad> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Ciudad_.id));
            }
            if (criteria.getNombre() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNombre(), Ciudad_.nombre));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Ciudad_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Ciudad_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), Ciudad_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), Ciudad_.lastModifiedDate));
            }
        }
        return specification;
    }
}
