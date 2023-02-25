package com.labreferencia.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ReporteSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("descripcion", table, columnPrefix + "_descripcion"));
        columns.add(Column.aliased("fecha", table, columnPrefix + "_fecha"));

        columns.add(Column.aliased("centro_medico_id", table, columnPrefix + "_centro_medico_id"));
        columns.add(Column.aliased("doctor_id", table, columnPrefix + "_doctor_id"));
        return columns;
    }
}
