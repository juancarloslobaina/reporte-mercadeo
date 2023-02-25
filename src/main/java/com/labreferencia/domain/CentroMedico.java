package com.labreferencia.domain;

import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A CentroMedico.
 */
@Table("centro_medico")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "centromedico")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CentroMedico implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("nombre_centro_medico")
    private String nombreCentroMedico;

    @Transient
    private Ciudad ciudad;

    @Column("ciudad_id")
    private Long ciudadId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CentroMedico id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreCentroMedico() {
        return this.nombreCentroMedico;
    }

    public CentroMedico nombreCentroMedico(String nombreCentroMedico) {
        this.setNombreCentroMedico(nombreCentroMedico);
        return this;
    }

    public void setNombreCentroMedico(String nombreCentroMedico) {
        this.nombreCentroMedico = nombreCentroMedico;
    }

    public Ciudad getCiudad() {
        return this.ciudad;
    }

    public void setCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
        this.ciudadId = ciudad != null ? ciudad.getId() : null;
    }

    public CentroMedico ciudad(Ciudad ciudad) {
        this.setCiudad(ciudad);
        return this;
    }

    public Long getCiudadId() {
        return this.ciudadId;
    }

    public void setCiudadId(Long ciudad) {
        this.ciudadId = ciudad;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CentroMedico)) {
            return false;
        }
        return id != null && id.equals(((CentroMedico) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CentroMedico{" +
            "id=" + getId() +
            ", nombreCentroMedico='" + getNombreCentroMedico() + "'" +
            "}";
    }
}
