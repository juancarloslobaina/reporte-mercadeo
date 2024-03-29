package com.labreferencia.repository;

import com.labreferencia.domain.Centro;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Centro entity.
 */
@Repository
public interface CentroRepository extends JpaRepository<Centro, Long> {
    default Optional<Centro> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Centro> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Centro> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct centro from Centro centro left join fetch centro.ciudad",
        countQuery = "select count(distinct centro) from Centro centro"
    )
    Page<Centro> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct centro from Centro centro left join fetch centro.ciudad")
    List<Centro> findAllWithToOneRelationships();

    @Query("select centro from Centro centro left join fetch centro.ciudad where centro.id =:id")
    Optional<Centro> findOneWithToOneRelationships(@Param("id") Long id);
}
