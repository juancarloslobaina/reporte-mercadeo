package com.labreferencia.reportemercadeo.service.mapper;

import com.labreferencia.reportemercadeo.domain.Ciudad;
import com.labreferencia.reportemercadeo.service.dto.CiudadDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Ciudad} and its DTO {@link CiudadDTO}.
 */
@Mapper(componentModel = "spring")
public interface CiudadMapper extends EntityMapper<CiudadDTO, Ciudad> {}
