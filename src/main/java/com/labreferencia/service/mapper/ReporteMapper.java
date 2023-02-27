package com.labreferencia.service.mapper;

import com.labreferencia.domain.Centro;
import com.labreferencia.domain.Doctor;
import com.labreferencia.domain.Reporte;
import com.labreferencia.service.dto.CentroDTO;
import com.labreferencia.service.dto.DoctorDTO;
import com.labreferencia.service.dto.ReporteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Reporte} and its DTO {@link ReporteDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReporteMapper extends EntityMapper<ReporteDTO, Reporte> {
    @Mapping(target = "centro", source = "centro", qualifiedByName = "centroNombre")
    @Mapping(target = "doctor", source = "doctor", qualifiedByName = "doctorNombre")
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
    DoctorDTO toDtoDoctorNombre(Doctor doctor);
}
