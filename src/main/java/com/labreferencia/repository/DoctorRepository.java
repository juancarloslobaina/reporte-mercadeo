package com.labreferencia.repository;

import com.labreferencia.domain.Doctor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Doctor entity.
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    default Optional<Doctor> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Doctor> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Doctor> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct doctor from Doctor doctor left join fetch doctor.especialidad",
        countQuery = "select count(distinct doctor) from Doctor doctor"
    )
    Page<Doctor> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct doctor from Doctor doctor left join fetch doctor.especialidad")
    List<Doctor> findAllWithToOneRelationships();

    @Query("select doctor from Doctor doctor left join fetch doctor.especialidad where doctor.id =:id")
    Optional<Doctor> findOneWithToOneRelationships(@Param("id") Long id);
}
