package com.labreferencia.service.mapper;

import com.labreferencia.domain.Centro;
import com.labreferencia.domain.Ciudad;
import com.labreferencia.service.dto.CentroDTO;
import com.labreferencia.service.dto.CiudadDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Centro} and its DTO {@link CentroDTO}.
 */
@Mapper(componentModel = "spring")
public interface CentroMapper extends EntityMapper<CentroDTO, Centro> {
    @Mapping(target = "ciudad", source = "ciudad", qualifiedByName = "ciudadNombre")
    CentroDTO toDto(Centro s);

    @Named("ciudadNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    CiudadDTO toDtoCiudadNombre(Ciudad ciudad);
}
