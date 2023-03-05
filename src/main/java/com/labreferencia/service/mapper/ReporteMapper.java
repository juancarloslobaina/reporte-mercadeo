package com.labreferencia.service.mapper;

import com.labreferencia.domain.*;
import com.labreferencia.service.dto.*;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Reporte} and its DTO {@link ReporteDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReporteMapper extends EntityMapper<ReporteDTO, Reporte> {
    @Mapping(target = "centro", source = "centro", qualifiedByName = "centroNombre")
    @Mapping(target = "doctor", source = "doctor", qualifiedByName = "doctorNombre")
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    ReporteDTO toDto(Reporte s);

    @Named("centroNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    CentroDTO toDtoCentroNombre(Centro centro);

    @Named("doctorNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "telefonoPersonal", source = "telefonoPersonal")
    @Mapping(target = "telefonoCorporativo", source = "telefonoCorporativo")
    @Mapping(target = "correoPersonal", source = "correoPersonal")
    @Mapping(target = "correoCorporativo", source = "correoCorporativo")
    @Mapping(target = "especialidad", source = "especialidad", qualifiedByName = "especialidadDescripcion")
    DoctorDTO toDtoDoctorNombre(Doctor doctor);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("especialidadDescripcion")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "descripcion", source = "descripcion")
    EspecialidadDTO toDtoDoctorEspecialidadDescripcion(Especialidad especialidad);
}
