package com.labreferencia.service;

import com.labreferencia.domain.*; // for static metamodels
import com.labreferencia.domain.Doctor;
import com.labreferencia.repository.DoctorRepository;
import com.labreferencia.service.criteria.DoctorCriteria;
import com.labreferencia.service.dto.DoctorDTO;
import com.labreferencia.service.mapper.DoctorMapper;
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
 * Service for executing complex queries for {@link Doctor} entities in the database.
 * The main input is a {@link DoctorCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link DoctorDTO} or a {@link Page} of {@link DoctorDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DoctorQueryService extends QueryService<Doctor> {

    private final Logger log = LoggerFactory.getLogger(DoctorQueryService.class);

    private final DoctorRepository doctorRepository;

    private final DoctorMapper doctorMapper;

    public DoctorQueryService(DoctorRepository doctorRepository, DoctorMapper doctorMapper) {
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
    }

    /**
     * Return a {@link List} of {@link DoctorDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<DoctorDTO> findByCriteria(DoctorCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Doctor> specification = createSpecification(criteria);
        return doctorMapper.toDto(doctorRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link DoctorDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DoctorDTO> findByCriteria(DoctorCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Doctor> specification = createSpecification(criteria);
        return doctorRepository.findAll(specification, page).map(doctorMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DoctorCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Doctor> specification = createSpecification(criteria);
        return doctorRepository.count(specification);
    }

    /**
     * Function to convert {@link DoctorCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Doctor> createSpecification(DoctorCriteria criteria) {
        Specification<Doctor> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Doctor_.id));
            }
            if (criteria.getNombre() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNombre(), Doctor_.nombre));
            }
            if (criteria.getCorreoPersonal() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCorreoPersonal(), Doctor_.correoPersonal));
            }
            if (criteria.getCorreoCorporativo() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCorreoCorporativo(), Doctor_.correoCorporativo));
            }
            if (criteria.getTelefonoPersonal() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTelefonoPersonal(), Doctor_.telefonoPersonal));
            }
            if (criteria.getTelefonoCorporativo() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTelefonoCorporativo(), Doctor_.telefonoCorporativo));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Doctor_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Doctor_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), Doctor_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), Doctor_.lastModifiedDate));
            }
            if (criteria.getEspecialidadId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getEspecialidadId(),
                            root -> root.join(Doctor_.especialidad, JoinType.LEFT).get(Especialidad_.id)
                        )
                    );
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(Doctor_.user, JoinType.LEFT).get(User_.id))
                    );
            }
        }
        return specification;
    }
}
