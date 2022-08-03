package com.labreferencia.reportemercadeo.service.mapper;

import com.labreferencia.reportemercadeo.domain.Especialidad;
import com.labreferencia.reportemercadeo.service.dto.EspecialidadDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Especialidad} and its DTO {@link EspecialidadDTO}.
 */
@Mapper(componentModel = "spring")
public interface EspecialidadMapper extends EntityMapper<EspecialidadDTO, Especialidad> {}
