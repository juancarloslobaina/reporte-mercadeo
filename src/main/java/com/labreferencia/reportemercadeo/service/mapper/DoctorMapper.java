package com.labreferencia.reportemercadeo.service.mapper;

import com.labreferencia.reportemercadeo.domain.Doctor;
import com.labreferencia.reportemercadeo.domain.Especialidad;
import com.labreferencia.reportemercadeo.service.dto.DoctorDTO;
import com.labreferencia.reportemercadeo.service.dto.EspecialidadDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Doctor} and its DTO {@link DoctorDTO}.
 */
@Mapper(componentModel = "spring")
public interface DoctorMapper extends EntityMapper<DoctorDTO, Doctor> {
    @Mapping(target = "especialidad", source = "especialidad", qualifiedByName = "especialidadNombreEspecialidad")
    DoctorDTO toDto(Doctor s);

    @Named("especialidadNombreEspecialidad")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombreEspecialidad", source = "nombreEspecialidad")
    EspecialidadDTO toDtoEspecialidadNombreEspecialidad(Especialidad especialidad);
}
