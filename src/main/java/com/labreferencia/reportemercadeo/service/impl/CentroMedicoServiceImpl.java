package com.labreferencia.reportemercadeo.service.impl;

import com.labreferencia.reportemercadeo.domain.CentroMedico;
import com.labreferencia.reportemercadeo.repository.CentroMedicoRepository;
import com.labreferencia.reportemercadeo.service.CentroMedicoService;
import com.labreferencia.reportemercadeo.service.dto.CentroMedicoDTO;
import com.labreferencia.reportemercadeo.service.mapper.CentroMedicoMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link CentroMedico}.
 */
@Service
@Transactional
public class CentroMedicoServiceImpl implements CentroMedicoService {

    private final Logger log = LoggerFactory.getLogger(CentroMedicoServiceImpl.class);

    private final CentroMedicoRepository centroMedicoRepository;

    private final CentroMedicoMapper centroMedicoMapper;

    public CentroMedicoServiceImpl(CentroMedicoRepository centroMedicoRepository, CentroMedicoMapper centroMedicoMapper) {
        this.centroMedicoRepository = centroMedicoRepository;
        this.centroMedicoMapper = centroMedicoMapper;
    }

    @Override
    public CentroMedicoDTO save(CentroMedicoDTO centroMedicoDTO) {
        log.debug("Request to save CentroMedico : {}", centroMedicoDTO);
        CentroMedico centroMedico = centroMedicoMapper.toEntity(centroMedicoDTO);
        centroMedico = centroMedicoRepository.save(centroMedico);
        return centroMedicoMapper.toDto(centroMedico);
    }

    @Override
    public CentroMedicoDTO update(CentroMedicoDTO centroMedicoDTO) {
        log.debug("Request to save CentroMedico : {}", centroMedicoDTO);
        CentroMedico centroMedico = centroMedicoMapper.toEntity(centroMedicoDTO);
        centroMedico = centroMedicoRepository.save(centroMedico);
        return centroMedicoMapper.toDto(centroMedico);
    }

    @Override
    public Optional<CentroMedicoDTO> partialUpdate(CentroMedicoDTO centroMedicoDTO) {
        log.debug("Request to partially update CentroMedico : {}", centroMedicoDTO);

        return centroMedicoRepository
            .findById(centroMedicoDTO.getId())
            .map(existingCentroMedico -> {
                centroMedicoMapper.partialUpdate(existingCentroMedico, centroMedicoDTO);

                return existingCentroMedico;
            })
            .map(centroMedicoRepository::save)
            .map(centroMedicoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CentroMedicoDTO> findAll(Pageable pageable) {
        log.debug("Request to get all CentroMedicos");
        return centroMedicoRepository.findAll(pageable).map(centroMedicoMapper::toDto);
    }

    public Page<CentroMedicoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return centroMedicoRepository.findAllWithEagerRelationships(pageable).map(centroMedicoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CentroMedicoDTO> findOne(Long id) {
        log.debug("Request to get CentroMedico : {}", id);
        return centroMedicoRepository.findOneWithEagerRelationships(id).map(centroMedicoMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete CentroMedico : {}", id);
        centroMedicoRepository.deleteById(id);
    }
}
