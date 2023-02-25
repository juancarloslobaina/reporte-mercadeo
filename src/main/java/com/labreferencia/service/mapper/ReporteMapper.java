package com.labreferencia.service.mapper;

import com.labreferencia.domain.CentroMedico;
import com.labreferencia.domain.Doctor;
import com.labreferencia.domain.Reporte;
import com.labreferencia.service.dto.CentroMedicoDTO;
import com.labreferencia.service.dto.DoctorDTO;
import com.labreferencia.service.dto.ReporteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Reporte} and its DTO {@link ReporteDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReporteMapper extends EntityMapper<ReporteDTO, Reporte> {
    @Mapping(target = "centroMedico", source = "centroMedico", qualifiedByName = "centroMedicoNombreCentroMedico")
    @Mapping(target = "doctor", source = "doctor", qualifiedByName = "doctorNombreDoctor")
    ReporteDTO toDto(Reporte s);

    @Named("centroMedicoNombreCentroMedico")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombreCentroMedico", source = "nombreCentroMedico")
    CentroMedicoDTO toDtoCentroMedicoNombreCentroMedico(CentroMedico centroMedico);

    @Named("doctorNombreDoctor")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombreDoctor", source = "nombreDoctor")
    DoctorDTO toDtoDoctorNombreDoctor(Doctor doctor);
}
