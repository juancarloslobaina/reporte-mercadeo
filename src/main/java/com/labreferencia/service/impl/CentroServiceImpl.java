package com.labreferencia.service.impl;

import com.labreferencia.domain.Centro;
import com.labreferencia.repository.CentroRepository;
import com.labreferencia.service.CentroService;
import com.labreferencia.service.dto.CentroDTO;
import com.labreferencia.service.mapper.CentroMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Centro}.
 */
@Service
@Transactional
public class CentroServiceImpl implements CentroService {

    private final Logger log = LoggerFactory.getLogger(CentroServiceImpl.class);

    private final CentroRepository centroRepository;

    private final CentroMapper centroMapper;

    public CentroServiceImpl(CentroRepository centroRepository, CentroMapper centroMapper) {
        this.centroRepository = centroRepository;
        this.centroMapper = centroMapper;
    }

    @Override
    public CentroDTO save(CentroDTO centroDTO) {
        log.debug("Request to save Centro : {}", centroDTO);
        Centro centro = centroMapper.toEntity(centroDTO);
        centro = centroRepository.save(centro);
        return centroMapper.toDto(centro);
    }

    @Override
    public CentroDTO update(CentroDTO centroDTO) {
        log.debug("Request to update Centro : {}", centroDTO);
        Centro centro = centroMapper.toEntity(centroDTO);
        centro = centroRepository.save(centro);
        return centroMapper.toDto(centro);
    }

    @Override
    public Optional<CentroDTO> partialUpdate(CentroDTO centroDTO) {
        log.debug("Request to partially update Centro : {}", centroDTO);

        return centroRepository
            .findById(centroDTO.getId())
            .map(existingCentro -> {
                centroMapper.partialUpdate(existingCentro, centroDTO);

                return existingCentro;
            })
            .map(centroRepository::save)
            .map(centroMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CentroDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Centros");
        return centroRepository.findAll(pageable).map(centroMapper::toDto);
    }

    public Page<CentroDTO> findAllWithEagerRelationships(Pageable pageable) {
        return centroRepository.findAllWithEagerRelationships(pageable).map(centroMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CentroDTO> findOne(Long id) {
        log.debug("Request to get Centro : {}", id);
        return centroRepository.findOneWithEagerRelationships(id).map(centroMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Centro : {}", id);
        centroRepository.deleteById(id);
    }
}
