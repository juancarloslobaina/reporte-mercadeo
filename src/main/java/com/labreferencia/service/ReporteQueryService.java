package com.labreferencia.service;

import com.labreferencia.domain.*; // for static metamodels
import com.labreferencia.domain.Reporte;
import com.labreferencia.repository.ReporteRepository;
import com.labreferencia.service.criteria.ReporteCriteria;
import com.labreferencia.service.dto.ReporteDTO;
import com.labreferencia.service.mapper.ReporteMapper;
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
 * Service for executing complex queries for {@link Reporte} entities in the database.
 * The main input is a {@link ReporteCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ReporteDTO} or a {@link Page} of {@link ReporteDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ReporteQueryService extends QueryService<Reporte> {

    private final Logger log = LoggerFactory.getLogger(ReporteQueryService.class);

    private final ReporteRepository reporteRepository;

    private final ReporteMapper reporteMapper;

    public ReporteQueryService(ReporteRepository reporteRepository, ReporteMapper reporteMapper) {
        this.reporteRepository = reporteRepository;
        this.reporteMapper = reporteMapper;
    }

    /**
     * Return a {@link List} of {@link ReporteDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ReporteDTO> findByCriteria(ReporteCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Reporte> specification = createSpecification(criteria);
        return reporteMapper.toDto(reporteRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ReporteDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ReporteDTO> findByCriteria(ReporteCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Reporte> specification = createSpecification(criteria);
        return reporteRepository.findAll(specification, page).map(reporteMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ReporteCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Reporte> specification = createSpecification(criteria);
        return reporteRepository.count(specification);
    }

    /**
     * Function to convert {@link ReporteCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Reporte> createSpecification(ReporteCriteria criteria) {
        Specification<Reporte> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Reporte_.id));
            }
            if (criteria.getDescripcion() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescripcion(), Reporte_.descripcion));
            }
            if (criteria.getFecha() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getFecha(), Reporte_.fecha));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Reporte_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Reporte_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), Reporte_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), Reporte_.lastModifiedDate));
            }
            if (criteria.getCentroId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getCentroId(), root -> root.join(Reporte_.centro, JoinType.LEFT).get(Centro_.id))
                    );
            }
            if (criteria.getDoctorId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getDoctorId(), root -> root.join(Reporte_.doctor, JoinType.LEFT).get(Doctor_.id))
                    );
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(Reporte_.user, JoinType.LEFT).get(User_.id))
                    );
            }
        }
        return specification;
    }
}
