package com.labreferencia.reportemercadeo.repository;

import com.labreferencia.reportemercadeo.domain.Ciudad;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Ciudad entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CiudadRepository extends JpaRepository<Ciudad, Long> {}
