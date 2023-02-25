package com.labreferencia.repository;

import com.labreferencia.domain.Reporte;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Reporte entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReporteRepository extends ReactiveCrudRepository<Reporte, Long>, ReporteRepositoryInternal {
    Flux<Reporte> findAllBy(Pageable pageable);

    @Override
    Mono<Reporte> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Reporte> findAllWithEagerRelationships();

    @Override
    Flux<Reporte> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM reporte entity WHERE entity.centro_medico_id = :id")
    Flux<Reporte> findByCentroMedico(Long id);

    @Query("SELECT * FROM reporte entity WHERE entity.centro_medico_id IS NULL")
    Flux<Reporte> findAllWhereCentroMedicoIsNull();

    @Query("SELECT * FROM reporte entity WHERE entity.doctor_id = :id")
    Flux<Reporte> findByDoctor(Long id);

    @Query("SELECT * FROM reporte entity WHERE entity.doctor_id IS NULL")
    Flux<Reporte> findAllWhereDoctorIsNull();

    @Override
    <S extends Reporte> Mono<S> save(S entity);

    @Override
    Flux<Reporte> findAll();

    @Override
    Mono<Reporte> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ReporteRepositoryInternal {
    <S extends Reporte> Mono<S> save(S entity);

    Flux<Reporte> findAllBy(Pageable pageable);

    Flux<Reporte> findAll();

    Mono<Reporte> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Reporte> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Reporte> findOneWithEagerRelationships(Long id);

    Flux<Reporte> findAllWithEagerRelationships();

    Flux<Reporte> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
