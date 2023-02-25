package com.labreferencia.repository.rowmapper;

import com.labreferencia.domain.Ciudad;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Ciudad}, with proper type conversions.
 */
@Service
public class CiudadRowMapper implements BiFunction<Row, String, Ciudad> {

    private final ColumnConverter converter;

    public CiudadRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Ciudad} stored in the database.
     */
    @Override
    public Ciudad apply(Row row, String prefix) {
        Ciudad entity = new Ciudad();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNombreCiudad(converter.fromRow(row, prefix + "_nombre_ciudad", String.class));
        return entity;
    }
}
