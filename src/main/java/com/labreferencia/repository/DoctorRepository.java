package com.labreferencia.repository;

import com.labreferencia.domain.Doctor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Doctor entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DoctorRepository extends ReactiveCrudRepository<Doctor, Long>, DoctorRepositoryInternal {
    Flux<Doctor> findAllBy(Pageable pageable);

    @Override
    Mono<Doctor> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Doctor> findAllWithEagerRelationships();

    @Override
    Flux<Doctor> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM doctor entity WHERE entity.especialidad_id = :id")
    Flux<Doctor> findByEspecialidad(Long id);

    @Query("SELECT * FROM doctor entity WHERE entity.especialidad_id IS NULL")
    Flux<Doctor> findAllWhereEspecialidadIsNull();

    @Override
    <S extends Doctor> Mono<S> save(S entity);

    @Override
    Flux<Doctor> findAll();

    @Override
    Mono<Doctor> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface DoctorRepositoryInternal {
    <S extends Doctor> Mono<S> save(S entity);

    Flux<Doctor> findAllBy(Pageable pageable);

    Flux<Doctor> findAll();

    Mono<Doctor> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Doctor> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Doctor> findOneWithEagerRelationships(Long id);

    Flux<Doctor> findAllWithEagerRelationships();

    Flux<Doctor> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
