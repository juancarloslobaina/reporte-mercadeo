package com.labreferencia.reportemercadeo.service.mapper;

import com.labreferencia.reportemercadeo.domain.CentroMedico;
import com.labreferencia.reportemercadeo.domain.Doctor;
import com.labreferencia.reportemercadeo.domain.Reporte;
import com.labreferencia.reportemercadeo.service.dto.CentroMedicoDTO;
import com.labreferencia.reportemercadeo.service.dto.DoctorDTO;
import com.labreferencia.reportemercadeo.service.dto.ReporteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Reporte} and its DTO {@link ReporteDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReporteMapper extends EntityMapper<ReporteDTO, Reporte> {
    @Mapping(target = "doctor", source = "doctor", qualifiedByName = "doctorNombreDoctor")
    @Mapping(target = "centroMedico", source = "centroMedico", qualifiedByName = "centroMedicoNombreCentroMedico")
    ReporteDTO toDto(Reporte s);

    @Named("doctorNombreDoctor")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombreDoctor", source = "nombreDoctor")
    DoctorDTO toDtoDoctorNombreDoctor(Doctor doctor);

    @Named("centroMedicoNombreCentroMedico")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombreCentroMedico", source = "nombreCentroMedico")
    CentroMedicoDTO toDtoCentroMedicoNombreCentroMedico(CentroMedico centroMedico);
}
