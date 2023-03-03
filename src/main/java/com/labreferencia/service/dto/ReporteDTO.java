package com.labreferencia.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.labreferencia.domain.Reporte} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReporteDTO implements Serializable {

    private Long id;

    @Lob
    private String descripcion;

    @NotNull
    private Instant fecha;

    private CentroDTO centro;

    private DoctorDTO doctor;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Instant getFecha() {
        return fecha;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    public CentroDTO getCentro() {
        return centro;
    }

    public void setCentro(CentroDTO centro) {
        this.centro = centro;
    }

    public DoctorDTO getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorDTO doctor) {
        this.doctor = doctor;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReporteDTO)) {
            return false;
        }

        ReporteDTO reporteDTO = (ReporteDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, reporteDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReporteDTO{" +
            "id=" + getId() +
            ", descripcion='" + getDescripcion() + "'" +
            ", fecha='" + getFecha() + "'" +
            ", centro=" + getCentro() +
            ", doctor=" + getDoctor() +
            ", user=" + getUser() +
            "}";
    }
}
