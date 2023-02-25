package com.labreferencia.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class DoctorSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("nombre_doctor", table, columnPrefix + "_nombre_doctor"));
        columns.add(Column.aliased("correo_personal", table, columnPrefix + "_correo_personal"));
        columns.add(Column.aliased("correo_corporativo", table, columnPrefix + "_correo_corporativo"));
        columns.add(Column.aliased("telefono_personal", table, columnPrefix + "_telefono_personal"));
        columns.add(Column.aliased("telefono_corporativo", table, columnPrefix + "_telefono_corporativo"));

        columns.add(Column.aliased("especialidad_id", table, columnPrefix + "_especialidad_id"));
        return columns;
    }
}
