package com.labreferencia.service.impl;

import com.labreferencia.domain.Reporte;
import com.labreferencia.repository.ReporteRepository;
import com.labreferencia.service.ReporteService;
import com.labreferencia.service.dto.ReporteDTO;
import com.labreferencia.service.mapper.ReporteMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Reporte}.
 */
@Service
@Transactional
public class ReporteServiceImpl implements ReporteService {

    private final Logger log = LoggerFactory.getLogger(ReporteServiceImpl.class);

    private final ReporteRepository reporteRepository;

    private final ReporteMapper reporteMapper;

    public ReporteServiceImpl(ReporteRepository reporteRepository, ReporteMapper reporteMapper) {
        this.reporteRepository = reporteRepository;
        this.reporteMapper = reporteMapper;
    }

    @Override
    public ReporteDTO save(ReporteDTO reporteDTO) {
        log.debug("Request to save Reporte : {}", reporteDTO);
        Reporte reporte = reporteMapper.toEntity(reporteDTO);
        reporte = reporteRepository.save(reporte);
        return reporteMapper.toDto(reporte);
    }

    @Override
    public ReporteDTO update(ReporteDTO reporteDTO) {
        log.debug("Request to update Reporte : {}", reporteDTO);
        Reporte reporte = reporteMapper.toEntity(reporteDTO);
        reporte = reporteRepository.save(reporte);
        return reporteMapper.toDto(reporte);
    }

    @Override
    public Optional<ReporteDTO> partialUpdate(ReporteDTO reporteDTO) {
        log.debug("Request to partially update Reporte : {}", reporteDTO);

        return reporteRepository
            .findById(reporteDTO.getId())
            .map(existingReporte -> {
                reporteMapper.partialUpdate(existingReporte, reporteDTO);

                return existingReporte;
            })
            .map(reporteRepository::save)
            .map(reporteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReporteDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Reportes");
        return reporteRepository.findAll(pageable).map(reporteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReporteDTO> findAll(Specification<Reporte> specification, Pageable pageable) {
        log.debug("Request to get all Reportes");
        return reporteRepository.findAll(specification, pageable).map(reporteMapper::toDto);
    }

    public Page<ReporteDTO> findAllWithEagerRelationships(Pageable pageable) {
        return reporteRepository.findAllWithEagerRelationships(pageable).map(reporteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReporteDTO> findOne(Long id) {
        log.debug("Request to get Reporte : {}", id);
        return reporteRepository.findOneWithEagerRelationships(id).map(reporteMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Reporte : {}", id);
        reporteRepository.deleteById(id);
    }
}
