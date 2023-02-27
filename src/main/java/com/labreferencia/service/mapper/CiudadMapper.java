package com.labreferencia.service.mapper;

import com.labreferencia.domain.Ciudad;
import com.labreferencia.service.dto.CiudadDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Ciudad} and its DTO {@link CiudadDTO}.
 */
@Mapper(componentModel = "spring")
public interface CiudadMapper extends EntityMapper<CiudadDTO, Ciudad> {}
