package com.labreferencia.service.impl;

import com.labreferencia.domain.Especialidad;
import com.labreferencia.repository.EspecialidadRepository;
import com.labreferencia.service.EspecialidadService;
import com.labreferencia.service.dto.EspecialidadDTO;
import com.labreferencia.service.mapper.EspecialidadMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Especialidad}.
 */
@Service
@Transactional
public class EspecialidadServiceImpl implements EspecialidadService {

    private final Logger log = LoggerFactory.getLogger(EspecialidadServiceImpl.class);

    private final EspecialidadRepository especialidadRepository;

    private final EspecialidadMapper especialidadMapper;

    public EspecialidadServiceImpl(EspecialidadRepository especialidadRepository, EspecialidadMapper especialidadMapper) {
        this.especialidadRepository = especialidadRepository;
        this.especialidadMapper = especialidadMapper;
    }

    @Override
    public EspecialidadDTO save(EspecialidadDTO especialidadDTO) {
        log.debug("Request to save Especialidad : {}", especialidadDTO);
        Especialidad especialidad = especialidadMapper.toEntity(especialidadDTO);
        especialidad = especialidadRepository.save(especialidad);
        return especialidadMapper.toDto(especialidad);
    }

    @Override
    public EspecialidadDTO update(EspecialidadDTO especialidadDTO) {
        log.debug("Request to update Especialidad : {}", especialidadDTO);
        Especialidad especialidad = especialidadMapper.toEntity(especialidadDTO);
        especialidad = especialidadRepository.save(especialidad);
        return especialidadMapper.toDto(especialidad);
    }

    @Override
    public Optional<EspecialidadDTO> partialUpdate(EspecialidadDTO especialidadDTO) {
        log.debug("Request to partially update Especialidad : {}", especialidadDTO);

        return especialidadRepository
            .findById(especialidadDTO.getId())
            .map(existingEspecialidad -> {
                especialidadMapper.partialUpdate(existingEspecialidad, especialidadDTO);

                return existingEspecialidad;
            })
            .map(especialidadRepository::save)
            .map(especialidadMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EspecialidadDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Especialidads");
        return especialidadRepository.findAll(pageable).map(especialidadMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EspecialidadDTO> findOne(Long id) {
        log.debug("Request to get Especialidad : {}", id);
        return especialidadRepository.findById(id).map(especialidadMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Especialidad : {}", id);
        especialidadRepository.deleteById(id);
    }
}
