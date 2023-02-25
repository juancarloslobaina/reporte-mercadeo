package com.labreferencia.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.labreferencia.domain.Especialidad;
import com.labreferencia.repository.EspecialidadRepository;
import com.labreferencia.repository.search.EspecialidadSearchRepository;
import com.labreferencia.service.EspecialidadService;
import com.labreferencia.service.dto.EspecialidadDTO;
import com.labreferencia.service.mapper.EspecialidadMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Especialidad}.
 */
@Service
@Transactional
public class EspecialidadServiceImpl implements EspecialidadService {

    private final Logger log = LoggerFactory.getLogger(EspecialidadServiceImpl.class);

    private final EspecialidadRepository especialidadRepository;

    private final EspecialidadMapper especialidadMapper;

    private final EspecialidadSearchRepository especialidadSearchRepository;

    public EspecialidadServiceImpl(
        EspecialidadRepository especialidadRepository,
        EspecialidadMapper especialidadMapper,
        EspecialidadSearchRepository especialidadSearchRepository
    ) {
        this.especialidadRepository = especialidadRepository;
        this.especialidadMapper = especialidadMapper;
        this.especialidadSearchRepository = especialidadSearchRepository;
    }

    @Override
    public Mono<EspecialidadDTO> save(EspecialidadDTO especialidadDTO) {
        log.debug("Request to save Especialidad : {}", especialidadDTO);
        return especialidadRepository
            .save(especialidadMapper.toEntity(especialidadDTO))
            .flatMap(especialidadSearchRepository::save)
            .map(especialidadMapper::toDto);
    }

    @Override
    public Mono<EspecialidadDTO> update(EspecialidadDTO especialidadDTO) {
        log.debug("Request to update Especialidad : {}", especialidadDTO);
        return especialidadRepository
            .save(especialidadMapper.toEntity(especialidadDTO))
            .flatMap(especialidadSearchRepository::save)
            .map(especialidadMapper::toDto);
    }

    @Override
    public Mono<EspecialidadDTO> partialUpdate(EspecialidadDTO especialidadDTO) {
        log.debug("Request to partially update Especialidad : {}", especialidadDTO);

        return especialidadRepository
            .findById(especialidadDTO.getId())
            .map(existingEspecialidad -> {
                especialidadMapper.partialUpdate(existingEspecialidad, especialidadDTO);

                return existingEspecialidad;
            })
            .flatMap(especialidadRepository::save)
            .flatMap(savedEspecialidad -> {
                especialidadSearchRepository.save(savedEspecialidad);

                return Mono.just(savedEspecialidad);
            })
            .map(especialidadMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EspecialidadDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Especialidads");
        return especialidadRepository.findAllBy(pageable).map(especialidadMapper::toDto);
    }

    public Mono<Long> countAll() {
        return especialidadRepository.count();
    }

    public Mono<Long> searchCount() {
        return especialidadSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<EspecialidadDTO> findOne(Long id) {
        log.debug("Request to get Especialidad : {}", id);
        return especialidadRepository.findById(id).map(especialidadMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Especialidad : {}", id);
        return especialidadRepository.deleteById(id).then(especialidadSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EspecialidadDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Especialidads for query {}", query);
        return especialidadSearchRepository.search(query, pageable).map(especialidadMapper::toDto);
    }
}
