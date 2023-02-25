package com.labreferencia.service.mapper;

import com.labreferencia.domain.Especialidad;
import com.labreferencia.service.dto.EspecialidadDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Especialidad} and its DTO {@link EspecialidadDTO}.
 */
@Mapper(componentModel = "spring")
public interface EspecialidadMapper extends EntityMapper<EspecialidadDTO, Especialidad> {}
