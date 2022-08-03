package com.labreferencia.reportemercadeo.repository;

import com.labreferencia.reportemercadeo.domain.CentroMedico;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CentroMedico entity.
 */
@Repository
public interface CentroMedicoRepository extends JpaRepository<CentroMedico, Long> {
    default Optional<CentroMedico> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<CentroMedico> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<CentroMedico> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct centroMedico from CentroMedico centroMedico left join fetch centroMedico.ciudad",
        countQuery = "select count(distinct centroMedico) from CentroMedico centroMedico"
    )
    Page<CentroMedico> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct centroMedico from CentroMedico centroMedico left join fetch centroMedico.ciudad")
    List<CentroMedico> findAllWithToOneRelationships();

    @Query("select centroMedico from CentroMedico centroMedico left join fetch centroMedico.ciudad where centroMedico.id =:id")
    Optional<CentroMedico> findOneWithToOneRelationships(@Param("id") Long id);
}
