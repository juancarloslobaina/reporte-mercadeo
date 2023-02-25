package com.labreferencia.repository.rowmapper;

import com.labreferencia.domain.CentroMedico;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link CentroMedico}, with proper type conversions.
 */
@Service
public class CentroMedicoRowMapper implements BiFunction<Row, String, CentroMedico> {

    private final ColumnConverter converter;

    public CentroMedicoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link CentroMedico} stored in the database.
     */
    @Override
    public CentroMedico apply(Row row, String prefix) {
        CentroMedico entity = new CentroMedico();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNombreCentroMedico(converter.fromRow(row, prefix + "_nombre_centro_medico", String.class));
        entity.setCiudadId(converter.fromRow(row, prefix + "_ciudad_id", Long.class));
        return entity;
    }
}
