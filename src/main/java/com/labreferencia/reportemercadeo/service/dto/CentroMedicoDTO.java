package com.labreferencia.reportemercadeo.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.labreferencia.reportemercadeo.domain.CentroMedico} entity.
 */
public class CentroMedicoDTO implements Serializable {

    private Long id;

    @NotNull
    private String nombreCentroMedico;

    private CiudadDTO ciudad;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreCentroMedico() {
        return nombreCentroMedico;
    }

    public void setNombreCentroMedico(String nombreCentroMedico) {
        this.nombreCentroMedico = nombreCentroMedico;
    }

    public CiudadDTO getCiudad() {
        return ciudad;
    }

    public void setCiudad(CiudadDTO ciudad) {
        this.ciudad = ciudad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CentroMedicoDTO)) {
            return false;
        }

        CentroMedicoDTO centroMedicoDTO = (CentroMedicoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, centroMedicoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CentroMedicoDTO{" +
            "id=" + getId() +
            ", nombreCentroMedico='" + getNombreCentroMedico() + "'" +
            ", ciudad=" + getCiudad() +
            "}";
    }
}
