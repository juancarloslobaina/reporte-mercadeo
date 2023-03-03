package com.labreferencia.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.labreferencia.domain.Doctor} entity. This class is used
 * in {@link com.labreferencia.web.rest.DoctorResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /doctors?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DoctorCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nombre;

    private StringFilter correoPersonal;

    private StringFilter correoCorporativo;

    private StringFilter telefonoPersonal;

    private StringFilter telefonoCorporativo;

    private LongFilter especialidadId;

    private LongFilter userId;

    private Boolean distinct;

    public DoctorCriteria() {}

    public DoctorCriteria(DoctorCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.nombre = other.nombre == null ? null : other.nombre.copy();
        this.correoPersonal = other.correoPersonal == null ? null : other.correoPersonal.copy();
        this.correoCorporativo = other.correoCorporativo == null ? null : other.correoCorporativo.copy();
        this.telefonoPersonal = other.telefonoPersonal == null ? null : other.telefonoPersonal.copy();
        this.telefonoCorporativo = other.telefonoCorporativo == null ? null : other.telefonoCorporativo.copy();
        this.especialidadId = other.especialidadId == null ? null : other.especialidadId.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public DoctorCriteria copy() {
        return new DoctorCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getNombre() {
        return nombre;
    }

    public StringFilter nombre() {
        if (nombre == null) {
            nombre = new StringFilter();
        }
        return nombre;
    }

    public void setNombre(StringFilter nombre) {
        this.nombre = nombre;
    }

    public StringFilter getCorreoPersonal() {
        return correoPersonal;
    }

    public StringFilter correoPersonal() {
        if (correoPersonal == null) {
            correoPersonal = new StringFilter();
        }
        return correoPersonal;
    }

    public void setCorreoPersonal(StringFilter correoPersonal) {
        this.correoPersonal = correoPersonal;
    }

    public StringFilter getCorreoCorporativo() {
        return correoCorporativo;
    }

    public StringFilter correoCorporativo() {
        if (correoCorporativo == null) {
            correoCorporativo = new StringFilter();
        }
        return correoCorporativo;
    }

    public void setCorreoCorporativo(StringFilter correoCorporativo) {
        this.correoCorporativo = correoCorporativo;
    }

    public StringFilter getTelefonoPersonal() {
        return telefonoPersonal;
    }

    public StringFilter telefonoPersonal() {
        if (telefonoPersonal == null) {
            telefonoPersonal = new StringFilter();
        }
        return telefonoPersonal;
    }

    public void setTelefonoPersonal(StringFilter telefonoPersonal) {
        this.telefonoPersonal = telefonoPersonal;
    }

    public StringFilter getTelefonoCorporativo() {
        return telefonoCorporativo;
    }

    public StringFilter telefonoCorporativo() {
        if (telefonoCorporativo == null) {
            telefonoCorporativo = new StringFilter();
        }
        return telefonoCorporativo;
    }

    public void setTelefonoCorporativo(StringFilter telefonoCorporativo) {
        this.telefonoCorporativo = telefonoCorporativo;
    }

    public LongFilter getEspecialidadId() {
        return especialidadId;
    }

    public LongFilter especialidadId() {
        if (especialidadId == null) {
            especialidadId = new LongFilter();
        }
        return especialidadId;
    }

    public void setEspecialidadId(LongFilter especialidadId) {
        this.especialidadId = especialidadId;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public LongFilter userId() {
        if (userId == null) {
            userId = new LongFilter();
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DoctorCriteria that = (DoctorCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nombre, that.nombre) &&
            Objects.equals(correoPersonal, that.correoPersonal) &&
            Objects.equals(correoCorporativo, that.correoCorporativo) &&
            Objects.equals(telefonoPersonal, that.telefonoPersonal) &&
            Objects.equals(telefonoCorporativo, that.telefonoCorporativo) &&
            Objects.equals(especialidadId, that.especialidadId) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            nombre,
            correoPersonal,
            correoCorporativo,
            telefonoPersonal,
            telefonoCorporativo,
            especialidadId,
            userId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DoctorCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (nombre != null ? "nombre=" + nombre + ", " : "") +
            (correoPersonal != null ? "correoPersonal=" + correoPersonal + ", " : "") +
            (correoCorporativo != null ? "correoCorporativo=" + correoCorporativo + ", " : "") +
            (telefonoPersonal != null ? "telefonoPersonal=" + telefonoPersonal + ", " : "") +
            (telefonoCorporativo != null ? "telefonoCorporativo=" + telefonoCorporativo + ", " : "") +
            (especialidadId != null ? "especialidadId=" + especialidadId + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
