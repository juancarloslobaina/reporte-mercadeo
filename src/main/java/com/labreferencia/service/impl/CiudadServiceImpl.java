package com.labreferencia.service.impl;

import com.labreferencia.domain.Ciudad;
import com.labreferencia.repository.CiudadRepository;
import com.labreferencia.service.CiudadService;
import com.labreferencia.service.dto.CiudadDTO;
import com.labreferencia.service.mapper.CiudadMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Ciudad}.
 */
@Service
@Transactional
public class CiudadServiceImpl implements CiudadService {

    private final Logger log = LoggerFactory.getLogger(CiudadServiceImpl.class);

    private final CiudadRepository ciudadRepository;

    private final CiudadMapper ciudadMapper;

    public CiudadServiceImpl(CiudadRepository ciudadRepository, CiudadMapper ciudadMapper) {
        this.ciudadRepository = ciudadRepository;
        this.ciudadMapper = ciudadMapper;
    }

    @Override
    public CiudadDTO save(CiudadDTO ciudadDTO) {
        log.debug("Request to save Ciudad : {}", ciudadDTO);
        Ciudad ciudad = ciudadMapper.toEntity(ciudadDTO);
        ciudad = ciudadRepository.save(ciudad);
        return ciudadMapper.toDto(ciudad);
    }

    @Override
    public CiudadDTO update(CiudadDTO ciudadDTO) {
        log.debug("Request to update Ciudad : {}", ciudadDTO);
        Ciudad ciudad = ciudadMapper.toEntity(ciudadDTO);
        ciudad = ciudadRepository.save(ciudad);
        return ciudadMapper.toDto(ciudad);
    }

    @Override
    public Optional<CiudadDTO> partialUpdate(CiudadDTO ciudadDTO) {
        log.debug("Request to partially update Ciudad : {}", ciudadDTO);

        return ciudadRepository
            .findById(ciudadDTO.getId())
            .map(existingCiudad -> {
                ciudadMapper.partialUpdate(existingCiudad, ciudadDTO);

                return existingCiudad;
            })
            .map(ciudadRepository::save)
            .map(ciudadMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CiudadDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Ciudads");
        return ciudadRepository.findAll(pageable).map(ciudadMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CiudadDTO> findOne(Long id) {
        log.debug("Request to get Ciudad : {}", id);
        return ciudadRepository.findById(id).map(ciudadMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Ciudad : {}", id);
        ciudadRepository.deleteById(id);
    }
}
