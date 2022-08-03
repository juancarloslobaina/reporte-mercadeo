package com.labreferencia.reportemercadeo.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A CentroMedico.
 */
@Entity
@Table(name = "centro_medico")
public class CentroMedico implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nombre_centro_medico", nullable = false)
    private String nombreCentroMedico;

    @OneToOne
    @JoinColumn(unique = true)
    private Ciudad ciudad;

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
    }

    public CentroMedico ciudad(Ciudad ciudad) {
        this.setCiudad(ciudad);
        return this;
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
