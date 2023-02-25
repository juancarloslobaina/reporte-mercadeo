package com.labreferencia.domain;

import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Doctor.
 */
@Table("doctor")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "doctor")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Doctor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("nombre_doctor")
    private String nombreDoctor;

    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column("correo_personal")
    private String correoPersonal;

    @NotNull(message = "must not be null")
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column("correo_corporativo")
    private String correoCorporativo;

    @NotNull(message = "must not be null")
    @Column("telefono_personal")
    private String telefonoPersonal;

    @Column("telefono_corporativo")
    private String telefonoCorporativo;

    @Transient
    private Especialidad especialidad;

    @Column("especialidad_id")
    private Long especialidadId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Doctor id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreDoctor() {
        return this.nombreDoctor;
    }

    public Doctor nombreDoctor(String nombreDoctor) {
        this.setNombreDoctor(nombreDoctor);
        return this;
    }

    public void setNombreDoctor(String nombreDoctor) {
        this.nombreDoctor = nombreDoctor;
    }

    public String getCorreoPersonal() {
        return this.correoPersonal;
    }

    public Doctor correoPersonal(String correoPersonal) {
        this.setCorreoPersonal(correoPersonal);
        return this;
    }

    public void setCorreoPersonal(String correoPersonal) {
        this.correoPersonal = correoPersonal;
    }

    public String getCorreoCorporativo() {
        return this.correoCorporativo;
    }

    public Doctor correoCorporativo(String correoCorporativo) {
        this.setCorreoCorporativo(correoCorporativo);
        return this;
    }

    public void setCorreoCorporativo(String correoCorporativo) {
        this.correoCorporativo = correoCorporativo;
    }

    public String getTelefonoPersonal() {
        return this.telefonoPersonal;
    }

    public Doctor telefonoPersonal(String telefonoPersonal) {
        this.setTelefonoPersonal(telefonoPersonal);
        return this;
    }

    public void setTelefonoPersonal(String telefonoPersonal) {
        this.telefonoPersonal = telefonoPersonal;
    }

    public String getTelefonoCorporativo() {
        return this.telefonoCorporativo;
    }

    public Doctor telefonoCorporativo(String telefonoCorporativo) {
        this.setTelefonoCorporativo(telefonoCorporativo);
        return this;
    }

    public void setTelefonoCorporativo(String telefonoCorporativo) {
        this.telefonoCorporativo = telefonoCorporativo;
    }

    public Especialidad getEspecialidad() {
        return this.especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
        this.especialidadId = especialidad != null ? especialidad.getId() : null;
    }

    public Doctor especialidad(Especialidad especialidad) {
        this.setEspecialidad(especialidad);
        return this;
    }

    public Long getEspecialidadId() {
        return this.especialidadId;
    }

    public void setEspecialidadId(Long especialidad) {
        this.especialidadId = especialidad;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Doctor)) {
            return false;
        }
        return id != null && id.equals(((Doctor) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Doctor{" +
            "id=" + getId() +
            ", nombreDoctor='" + getNombreDoctor() + "'" +
            ", correoPersonal='" + getCorreoPersonal() + "'" +
            ", correoCorporativo='" + getCorreoCorporativo() + "'" +
            ", telefonoPersonal='" + getTelefonoPersonal() + "'" +
            ", telefonoCorporativo='" + getTelefonoCorporativo() + "'" +
            "}";
    }
}
