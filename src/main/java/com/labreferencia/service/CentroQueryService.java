package com.labreferencia.service;

import com.labreferencia.domain.*; // for static metamodels
import com.labreferencia.domain.Centro;
import com.labreferencia.repository.CentroRepository;
import com.labreferencia.service.criteria.CentroCriteria;
import com.labreferencia.service.dto.CentroDTO;
import com.labreferencia.service.mapper.CentroMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Centro} entities in the database.
 * The main input is a {@link CentroCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CentroDTO} or a {@link Page} of {@link CentroDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CentroQueryService extends QueryService<Centro> {

    private final Logger log = LoggerFactory.getLogger(CentroQueryService.class);

    private final CentroRepository centroRepository;

    private final CentroMapper centroMapper;

    public CentroQueryService(CentroRepository centroRepository, CentroMapper centroMapper) {
        this.centroRepository = centroRepository;
        this.centroMapper = centroMapper;
    }

    /**
     * Return a {@link List} of {@link CentroDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CentroDTO> findByCriteria(CentroCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Centro> specification = createSpecification(criteria);
        return centroMapper.toDto(centroRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link CentroDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CentroDTO> findByCriteria(CentroCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Centro> specification = createSpecification(criteria);
        return centroRepository.findAll(specification, page).map(centroMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CentroCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Centro> specification = createSpecification(criteria);
        return centroRepository.count(specification);
    }

    /**
     * Function to convert {@link CentroCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Centro> createSpecification(CentroCriteria criteria) {
        Specification<Centro> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Centro_.id));
            }
            if (criteria.getNombre() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNombre(), Centro_.nombre));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Centro_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Centro_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), Centro_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), Centro_.lastModifiedDate));
            }
            if (criteria.getCiudadId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getCiudadId(), root -> root.join(Centro_.ciudad, JoinType.LEFT).get(Ciudad_.id))
                    );
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(Centro_.user, JoinType.LEFT).get(User_.id))
                    );
            }
        }
        return specification;
    }
}
