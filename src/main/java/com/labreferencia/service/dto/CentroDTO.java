package com.labreferencia.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.labreferencia.domain.Centro} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CentroDTO implements Serializable {

    private Long id;

    @NotNull
    private String nombre;

    private CiudadDTO ciudad;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
        if (!(o instanceof CentroDTO)) {
            return false;
        }

        CentroDTO centroDTO = (CentroDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, centroDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CentroDTO{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", ciudad=" + getCiudad() +
            "}";
    }
}
