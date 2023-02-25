package com.labreferencia.service.mapper;

import com.labreferencia.domain.CentroMedico;
import com.labreferencia.domain.Ciudad;
import com.labreferencia.service.dto.CentroMedicoDTO;
import com.labreferencia.service.dto.CiudadDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CentroMedico} and its DTO {@link CentroMedicoDTO}.
 */
@Mapper(componentModel = "spring")
public interface CentroMedicoMapper extends EntityMapper<CentroMedicoDTO, CentroMedico> {
    @Mapping(target = "ciudad", source = "ciudad", qualifiedByName = "ciudadNombreCiudad")
    CentroMedicoDTO toDto(CentroMedico s);

    @Named("ciudadNombreCiudad")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombreCiudad", source = "nombreCiudad")
    CiudadDTO toDtoCiudadNombreCiudad(Ciudad ciudad);
}
