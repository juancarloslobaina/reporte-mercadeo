package com.labreferencia.repository.rowmapper;

import com.labreferencia.domain.Doctor;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Doctor}, with proper type conversions.
 */
@Service
public class DoctorRowMapper implements BiFunction<Row, String, Doctor> {

    private final ColumnConverter converter;

    public DoctorRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Doctor} stored in the database.
     */
    @Override
    public Doctor apply(Row row, String prefix) {
        Doctor entity = new Doctor();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNombreDoctor(converter.fromRow(row, prefix + "_nombre_doctor", String.class));
        entity.setCorreoPersonal(converter.fromRow(row, prefix + "_correo_personal", String.class));
        entity.setCorreoCorporativo(converter.fromRow(row, prefix + "_correo_corporativo", String.class));
        entity.setTelefonoPersonal(converter.fromRow(row, prefix + "_telefono_personal", String.class));
        entity.setTelefonoCorporativo(converter.fromRow(row, prefix + "_telefono_corporativo", String.class));
        entity.setEspecialidadId(converter.fromRow(row, prefix + "_especialidad_id", Long.class));
        return entity;
    }
}
