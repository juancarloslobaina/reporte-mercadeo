package com.labreferencia.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.labreferencia.domain.Reporte;
import com.labreferencia.repository.rowmapper.CentroMedicoRowMapper;
import com.labreferencia.repository.rowmapper.DoctorRowMapper;
import com.labreferencia.repository.rowmapper.ReporteRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Reporte entity.
 */
@SuppressWarnings("unused")
class ReporteRepositoryInternalImpl extends SimpleR2dbcRepository<Reporte, Long> implements ReporteRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CentroMedicoRowMapper centromedicoMapper;
    private final DoctorRowMapper doctorMapper;
    private final ReporteRowMapper reporteMapper;

    private static final Table entityTable = Table.aliased("reporte", EntityManager.ENTITY_ALIAS);
    private static final Table centroMedicoTable = Table.aliased("centro_medico", "centroMedico");
    private static final Table doctorTable = Table.aliased("doctor", "doctor");

    public ReporteRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CentroMedicoRowMapper centromedicoMapper,
        DoctorRowMapper doctorMapper,
        ReporteRowMapper reporteMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Reporte.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.centromedicoMapper = centromedicoMapper;
        this.doctorMapper = doctorMapper;
        this.reporteMapper = reporteMapper;
    }

    @Override
    public Flux<Reporte> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Reporte> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ReporteSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CentroMedicoSqlHelper.getColumns(centroMedicoTable, "centroMedico"));
        columns.addAll(DoctorSqlHelper.getColumns(doctorTable, "doctor"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(centroMedicoTable)
            .on(Column.create("centro_medico_id", entityTable))
            .equals(Column.create("id", centroMedicoTable))
            .leftOuterJoin(doctorTable)
            .on(Column.create("doctor_id", entityTable))
            .equals(Column.create("id", doctorTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Reporte.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Reporte> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Reporte> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Reporte> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Reporte> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Reporte> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Reporte process(Row row, RowMetadata metadata) {
        Reporte entity = reporteMapper.apply(row, "e");
        entity.setCentroMedico(centromedicoMapper.apply(row, "centroMedico"));
        entity.setDoctor(doctorMapper.apply(row, "doctor"));
        return entity;
    }

    @Override
    public <S extends Reporte> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
