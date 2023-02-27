package com.labreferencia.service.mapper;

import com.labreferencia.domain.Doctor;
import com.labreferencia.domain.Especialidad;
import com.labreferencia.service.dto.DoctorDTO;
import com.labreferencia.service.dto.EspecialidadDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Doctor} and its DTO {@link DoctorDTO}.
 */
@Mapper(componentModel = "spring")
public interface DoctorMapper extends EntityMapper<DoctorDTO, Doctor> {
    @Mapping(target = "especialidad", source = "especialidad", qualifiedByName = "especialidadDescripcion")
    DoctorDTO toDto(Doctor s);

    @Named("especialidadDescripcion")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "descripcion", source = "descripcion")
    EspecialidadDTO toDtoEspecialidadDescripcion(Especialidad especialidad);
}
