package com.labreferencia.repository.rowmapper;

import com.labreferencia.domain.Reporte;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Reporte}, with proper type conversions.
 */
@Service
public class ReporteRowMapper implements BiFunction<Row, String, Reporte> {

    private final ColumnConverter converter;

    public ReporteRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Reporte} stored in the database.
     */
    @Override
    public Reporte apply(Row row, String prefix) {
        Reporte entity = new Reporte();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDescripcion(converter.fromRow(row, prefix + "_descripcion", String.class));
        entity.setFecha(converter.fromRow(row, prefix + "_fecha", Instant.class));
        entity.setCentroMedicoId(converter.fromRow(row, prefix + "_centro_medico_id", Long.class));
        entity.setDoctorId(converter.fromRow(row, prefix + "_doctor_id", Long.class));
        return entity;
    }
}
