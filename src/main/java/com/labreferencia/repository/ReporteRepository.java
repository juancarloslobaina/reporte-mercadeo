package com.labreferencia.repository;

import com.labreferencia.domain.Reporte;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Reporte entity.
 */
@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    default Optional<Reporte> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Reporte> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Reporte> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct reporte from Reporte reporte left join fetch reporte.centro left join fetch reporte.doctor",
        countQuery = "select count(distinct reporte) from Reporte reporte"
    )
    Page<Reporte> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct reporte from Reporte reporte left join fetch reporte.centro left join fetch reporte.doctor")
    List<Reporte> findAllWithToOneRelationships();

    @Query("select reporte from Reporte reporte left join fetch reporte.centro left join fetch reporte.doctor where reporte.id =:id")
    Optional<Reporte> findOneWithToOneRelationships(@Param("id") Long id);
}
