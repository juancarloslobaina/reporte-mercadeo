package com.labreferencia.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.labreferencia.domain.Ciudad;
import com.labreferencia.repository.CiudadRepository;
import com.labreferencia.repository.search.CiudadSearchRepository;
import com.labreferencia.service.CiudadService;
import com.labreferencia.service.dto.CiudadDTO;
import com.labreferencia.service.mapper.CiudadMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Ciudad}.
 */
@Service
@Transactional
public class CiudadServiceImpl implements CiudadService {

    private final Logger log = LoggerFactory.getLogger(CiudadServiceImpl.class);

    private final CiudadRepository ciudadRepository;

    private final CiudadMapper ciudadMapper;

    private final CiudadSearchRepository ciudadSearchRepository;

    public CiudadServiceImpl(CiudadRepository ciudadRepository, CiudadMapper ciudadMapper, CiudadSearchRepository ciudadSearchRepository) {
        this.ciudadRepository = ciudadRepository;
        this.ciudadMapper = ciudadMapper;
        this.ciudadSearchRepository = ciudadSearchRepository;
    }

    @Override
    public Mono<CiudadDTO> save(CiudadDTO ciudadDTO) {
        log.debug("Request to save Ciudad : {}", ciudadDTO);
        return ciudadRepository.save(ciudadMapper.toEntity(ciudadDTO)).flatMap(ciudadSearchRepository::save).map(ciudadMapper::toDto);
    }

    @Override
    public Mono<CiudadDTO> update(CiudadDTO ciudadDTO) {
        log.debug("Request to update Ciudad : {}", ciudadDTO);
        return ciudadRepository.save(ciudadMapper.toEntity(ciudadDTO)).flatMap(ciudadSearchRepository::save).map(ciudadMapper::toDto);
    }

    @Override
    public Mono<CiudadDTO> partialUpdate(CiudadDTO ciudadDTO) {
        log.debug("Request to partially update Ciudad : {}", ciudadDTO);

        return ciudadRepository
            .findById(ciudadDTO.getId())
            .map(existingCiudad -> {
                ciudadMapper.partialUpdate(existingCiudad, ciudadDTO);

                return existingCiudad;
            })
            .flatMap(ciudadRepository::save)
            .flatMap(savedCiudad -> {
                ciudadSearchRepository.save(savedCiudad);

                return Mono.just(savedCiudad);
            })
            .map(ciudadMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CiudadDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Ciudads");
        return ciudadRepository.findAllBy(pageable).map(ciudadMapper::toDto);
    }

    public Mono<Long> countAll() {
        return ciudadRepository.count();
    }

    public Mono<Long> searchCount() {
        return ciudadSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CiudadDTO> findOne(Long id) {
        log.debug("Request to get Ciudad : {}", id);
        return ciudadRepository.findById(id).map(ciudadMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Ciudad : {}", id);
        return ciudadRepository.deleteById(id).then(ciudadSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CiudadDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Ciudads for query {}", query);
        return ciudadSearchRepository.search(query, pageable).map(ciudadMapper::toDto);
    }
}
