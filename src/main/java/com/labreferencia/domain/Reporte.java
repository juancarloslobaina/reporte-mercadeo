package com.labreferencia.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Reporte.
 */
@Table("reporte")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "reporte")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Reporte implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("descripcion")
    private String descripcion;

    @NotNull(message = "must not be null")
    @Column("fecha")
    private Instant fecha;

    @Transient
    @JsonIgnoreProperties(value = { "ciudad" }, allowSetters = true)
    private CentroMedico centroMedico;

    @Transient
    @JsonIgnoreProperties(value = { "especialidad" }, allowSetters = true)
    private Doctor doctor;

    @Column("centro_medico_id")
    private Long centroMedicoId;

    @Column("doctor_id")
    private Long doctorId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Reporte id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public Reporte descripcion(String descripcion) {
        this.setDescripcion(descripcion);
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Instant getFecha() {
        return this.fecha;
    }

    public Reporte fecha(Instant fecha) {
        this.setFecha(fecha);
        return this;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    public CentroMedico getCentroMedico() {
        return this.centroMedico;
    }

    public void setCentroMedico(CentroMedico centroMedico) {
        this.centroMedico = centroMedico;
        this.centroMedicoId = centroMedico != null ? centroMedico.getId() : null;
    }

    public Reporte centroMedico(CentroMedico centroMedico) {
        this.setCentroMedico(centroMedico);
        return this;
    }

    public Doctor getDoctor() {
        return this.doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
        this.doctorId = doctor != null ? doctor.getId() : null;
    }

    public Reporte doctor(Doctor doctor) {
        this.setDoctor(doctor);
        return this;
    }

    public Long getCentroMedicoId() {
        return this.centroMedicoId;
    }

    public void setCentroMedicoId(Long centroMedico) {
        this.centroMedicoId = centroMedico;
    }

    public Long getDoctorId() {
        return this.doctorId;
    }

    public void setDoctorId(Long doctor) {
        this.doctorId = doctor;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reporte)) {
            return false;
        }
        return id != null && id.equals(((Reporte) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Reporte{" +
            "id=" + getId() +
            ", descripcion='" + getDescripcion() + "'" +
            ", fecha='" + getFecha() + "'" +
            "}";
    }
}
