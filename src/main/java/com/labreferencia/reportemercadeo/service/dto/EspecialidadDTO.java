package com.labreferencia.reportemercadeo.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.labreferencia.reportemercadeo.domain.Especialidad} entity.
 */
public class EspecialidadDTO implements Serializable {

    private Long id;

    @NotNull
    private String nombreEspecialidad;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreEspecialidad() {
        return nombreEspecialidad;
    }

    public void setNombreEspecialidad(String nombreEspecialidad) {
        this.nombreEspecialidad = nombreEspecialidad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EspecialidadDTO)) {
            return false;
        }

        EspecialidadDTO especialidadDTO = (EspecialidadDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, especialidadDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EspecialidadDTO{" +
            "id=" + getId() +
            ", nombreEspecialidad='" + getNombreEspecialidad() + "'" +
            "}";
    }
}
