package com.labreferencia.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.labreferencia.domain.Reporte;
import com.labreferencia.repository.ReporteRepository;
import com.labreferencia.repository.search.ReporteSearchRepository;
import com.labreferencia.service.ReporteService;
import com.labreferencia.service.dto.ReporteDTO;
import com.labreferencia.service.mapper.ReporteMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Reporte}.
 */
@Service
@Transactional
public class ReporteServiceImpl implements ReporteService {

    private final Logger log = LoggerFactory.getLogger(ReporteServiceImpl.class);

    private final ReporteRepository reporteRepository;

    private final ReporteMapper reporteMapper;

    private final ReporteSearchRepository reporteSearchRepository;

    public ReporteServiceImpl(
        ReporteRepository reporteRepository,
        ReporteMapper reporteMapper,
        ReporteSearchRepository reporteSearchRepository
    ) {
        this.reporteRepository = reporteRepository;
        this.reporteMapper = reporteMapper;
        this.reporteSearchRepository = reporteSearchRepository;
    }

    @Override
    public Mono<ReporteDTO> save(ReporteDTO reporteDTO) {
        log.debug("Request to save Reporte : {}", reporteDTO);
        return reporteRepository.save(reporteMapper.toEntity(reporteDTO)).flatMap(reporteSearchRepository::save).map(reporteMapper::toDto);
    }

    @Override
    public Mono<ReporteDTO> update(ReporteDTO reporteDTO) {
        log.debug("Request to update Reporte : {}", reporteDTO);
        return reporteRepository.save(reporteMapper.toEntity(reporteDTO)).flatMap(reporteSearchRepository::save).map(reporteMapper::toDto);
    }

    @Override
    public Mono<ReporteDTO> partialUpdate(ReporteDTO reporteDTO) {
        log.debug("Request to partially update Reporte : {}", reporteDTO);

        return reporteRepository
            .findById(reporteDTO.getId())
            .map(existingReporte -> {
                reporteMapper.partialUpdate(existingReporte, reporteDTO);

                return existingReporte;
            })
            .flatMap(reporteRepository::save)
            .flatMap(savedReporte -> {
                reporteSearchRepository.save(savedReporte);

                return Mono.just(savedReporte);
            })
            .map(reporteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ReporteDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Reportes");
        return reporteRepository.findAllBy(pageable).map(reporteMapper::toDto);
    }

    public Flux<ReporteDTO> findAllWithEagerRelationships(Pageable pageable) {
        return reporteRepository.findAllWithEagerRelationships(pageable).map(reporteMapper::toDto);
    }

    public Mono<Long> countAll() {
        return reporteRepository.count();
    }

    public Mono<Long> searchCount() {
        return reporteSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ReporteDTO> findOne(Long id) {
        log.debug("Request to get Reporte : {}", id);
        return reporteRepository.findOneWithEagerRelationships(id).map(reporteMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Reporte : {}", id);
        return reporteRepository.deleteById(id).then(reporteSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ReporteDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Reportes for query {}", query);
        return reporteSearchRepository.search(query, pageable).map(reporteMapper::toDto);
    }
}
