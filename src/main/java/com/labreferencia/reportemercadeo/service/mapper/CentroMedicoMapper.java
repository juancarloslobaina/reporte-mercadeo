package com.labreferencia.reportemercadeo.service.mapper;

import com.labreferencia.reportemercadeo.domain.CentroMedico;
import com.labreferencia.reportemercadeo.domain.Ciudad;
import com.labreferencia.reportemercadeo.service.dto.CentroMedicoDTO;
import com.labreferencia.reportemercadeo.service.dto.CiudadDTO;
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
