package com.labreferencia.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.labreferencia.domain.CentroMedico;
import com.labreferencia.repository.CentroMedicoRepository;
import com.labreferencia.repository.search.CentroMedicoSearchRepository;
import com.labreferencia.service.CentroMedicoService;
import com.labreferencia.service.dto.CentroMedicoDTO;
import com.labreferencia.service.mapper.CentroMedicoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link CentroMedico}.
 */
@Service
@Transactional
public class CentroMedicoServiceImpl implements CentroMedicoService {

    private final Logger log = LoggerFactory.getLogger(CentroMedicoServiceImpl.class);

    private final CentroMedicoRepository centroMedicoRepository;

    private final CentroMedicoMapper centroMedicoMapper;

    private final CentroMedicoSearchRepository centroMedicoSearchRepository;

    public CentroMedicoServiceImpl(
        CentroMedicoRepository centroMedicoRepository,
        CentroMedicoMapper centroMedicoMapper,
        CentroMedicoSearchRepository centroMedicoSearchRepository
    ) {
        this.centroMedicoRepository = centroMedicoRepository;
        this.centroMedicoMapper = centroMedicoMapper;
        this.centroMedicoSearchRepository = centroMedicoSearchRepository;
    }

    @Override
    public Mono<CentroMedicoDTO> save(CentroMedicoDTO centroMedicoDTO) {
        log.debug("Request to save CentroMedico : {}", centroMedicoDTO);
        return centroMedicoRepository
            .save(centroMedicoMapper.toEntity(centroMedicoDTO))
            .flatMap(centroMedicoSearchRepository::save)
            .map(centroMedicoMapper::toDto);
    }

    @Override
    public Mono<CentroMedicoDTO> update(CentroMedicoDTO centroMedicoDTO) {
        log.debug("Request to update CentroMedico : {}", centroMedicoDTO);
        return centroMedicoRepository
            .save(centroMedicoMapper.toEntity(centroMedicoDTO))
            .flatMap(centroMedicoSearchRepository::save)
            .map(centroMedicoMapper::toDto);
    }

    @Override
    public Mono<CentroMedicoDTO> partialUpdate(CentroMedicoDTO centroMedicoDTO) {
        log.debug("Request to partially update CentroMedico : {}", centroMedicoDTO);

        return centroMedicoRepository
            .findById(centroMedicoDTO.getId())
            .map(existingCentroMedico -> {
                centroMedicoMapper.partialUpdate(existingCentroMedico, centroMedicoDTO);

                return existingCentroMedico;
            })
            .flatMap(centroMedicoRepository::save)
            .flatMap(savedCentroMedico -> {
                centroMedicoSearchRepository.save(savedCentroMedico);

                return Mono.just(savedCentroMedico);
            })
            .map(centroMedicoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CentroMedicoDTO> findAll(Pageable pageable) {
        log.debug("Request to get all CentroMedicos");
        return centroMedicoRepository.findAllBy(pageable).map(centroMedicoMapper::toDto);
    }

    public Flux<CentroMedicoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return centroMedicoRepository.findAllWithEagerRelationships(pageable).map(centroMedicoMapper::toDto);
    }

    public Mono<Long> countAll() {
        return centroMedicoRepository.count();
    }

    public Mono<Long> searchCount() {
        return centroMedicoSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CentroMedicoDTO> findOne(Long id) {
        log.debug("Request to get CentroMedico : {}", id);
        return centroMedicoRepository.findOneWithEagerRelationships(id).map(centroMedicoMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete CentroMedico : {}", id);
        return centroMedicoRepository.deleteById(id).then(centroMedicoSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CentroMedicoDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of CentroMedicos for query {}", query);
        return centroMedicoSearchRepository.search(query, pageable).map(centroMedicoMapper::toDto);
    }
}
