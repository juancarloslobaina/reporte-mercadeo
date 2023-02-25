package com.labreferencia.repository;

import com.labreferencia.domain.CentroMedico;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the CentroMedico entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CentroMedicoRepository extends ReactiveCrudRepository<CentroMedico, Long>, CentroMedicoRepositoryInternal {
    Flux<CentroMedico> findAllBy(Pageable pageable);

    @Override
    Mono<CentroMedico> findOneWithEagerRelationships(Long id);

    @Override
    Flux<CentroMedico> findAllWithEagerRelationships();

    @Override
    Flux<CentroMedico> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM centro_medico entity WHERE entity.ciudad_id = :id")
    Flux<CentroMedico> findByCiudad(Long id);

    @Query("SELECT * FROM centro_medico entity WHERE entity.ciudad_id IS NULL")
    Flux<CentroMedico> findAllWhereCiudadIsNull();

    @Override
    <S extends CentroMedico> Mono<S> save(S entity);

    @Override
    Flux<CentroMedico> findAll();

    @Override
    Mono<CentroMedico> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CentroMedicoRepositoryInternal {
    <S extends CentroMedico> Mono<S> save(S entity);

    Flux<CentroMedico> findAllBy(Pageable pageable);

    Flux<CentroMedico> findAll();

    Mono<CentroMedico> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<CentroMedico> findAllBy(Pageable pageable, Criteria criteria);

    Mono<CentroMedico> findOneWithEagerRelationships(Long id);

    Flux<CentroMedico> findAllWithEagerRelationships();

    Flux<CentroMedico> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
