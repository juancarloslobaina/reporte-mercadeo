package com.labreferencia.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.labreferencia.domain.Doctor;
import com.labreferencia.repository.DoctorRepository;
import com.labreferencia.repository.search.DoctorSearchRepository;
import com.labreferencia.service.DoctorService;
import com.labreferencia.service.dto.DoctorDTO;
import com.labreferencia.service.mapper.DoctorMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Doctor}.
 */
@Service
@Transactional
public class DoctorServiceImpl implements DoctorService {

    private final Logger log = LoggerFactory.getLogger(DoctorServiceImpl.class);

    private final DoctorRepository doctorRepository;

    private final DoctorMapper doctorMapper;

    private final DoctorSearchRepository doctorSearchRepository;

    public DoctorServiceImpl(DoctorRepository doctorRepository, DoctorMapper doctorMapper, DoctorSearchRepository doctorSearchRepository) {
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
        this.doctorSearchRepository = doctorSearchRepository;
    }

    @Override
    public Mono<DoctorDTO> save(DoctorDTO doctorDTO) {
        log.debug("Request to save Doctor : {}", doctorDTO);
        return doctorRepository.save(doctorMapper.toEntity(doctorDTO)).flatMap(doctorSearchRepository::save).map(doctorMapper::toDto);
    }

    @Override
    public Mono<DoctorDTO> update(DoctorDTO doctorDTO) {
        log.debug("Request to update Doctor : {}", doctorDTO);
        return doctorRepository.save(doctorMapper.toEntity(doctorDTO)).flatMap(doctorSearchRepository::save).map(doctorMapper::toDto);
    }

    @Override
    public Mono<DoctorDTO> partialUpdate(DoctorDTO doctorDTO) {
        log.debug("Request to partially update Doctor : {}", doctorDTO);

        return doctorRepository
            .findById(doctorDTO.getId())
            .map(existingDoctor -> {
                doctorMapper.partialUpdate(existingDoctor, doctorDTO);

                return existingDoctor;
            })
            .flatMap(doctorRepository::save)
            .flatMap(savedDoctor -> {
                doctorSearchRepository.save(savedDoctor);

                return Mono.just(savedDoctor);
            })
            .map(doctorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DoctorDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Doctors");
        return doctorRepository.findAllBy(pageable).map(doctorMapper::toDto);
    }

    public Flux<DoctorDTO> findAllWithEagerRelationships(Pageable pageable) {
        return doctorRepository.findAllWithEagerRelationships(pageable).map(doctorMapper::toDto);
    }

    public Mono<Long> countAll() {
        return doctorRepository.count();
    }

    public Mono<Long> searchCount() {
        return doctorSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DoctorDTO> findOne(Long id) {
        log.debug("Request to get Doctor : {}", id);
        return doctorRepository.findOneWithEagerRelationships(id).map(doctorMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Doctor : {}", id);
        return doctorRepository.deleteById(id).then(doctorSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DoctorDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Doctors for query {}", query);
        return doctorSearchRepository.search(query, pageable).map(doctorMapper::toDto);
    }
}
