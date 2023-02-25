package com.labreferencia.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.labreferencia.domain.CentroMedico;
import com.labreferencia.repository.rowmapper.CentroMedicoRowMapper;
import com.labreferencia.repository.rowmapper.CiudadRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
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
 * Spring Data R2DBC custom repository implementation for the CentroMedico entity.
 */
@SuppressWarnings("unused")
class CentroMedicoRepositoryInternalImpl extends SimpleR2dbcRepository<CentroMedico, Long> implements CentroMedicoRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CiudadRowMapper ciudadMapper;
    private final CentroMedicoRowMapper centromedicoMapper;

    private static final Table entityTable = Table.aliased("centro_medico", EntityManager.ENTITY_ALIAS);
    private static final Table ciudadTable = Table.aliased("ciudad", "ciudad");

    public CentroMedicoRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CiudadRowMapper ciudadMapper,
        CentroMedicoRowMapper centromedicoMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(CentroMedico.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.ciudadMapper = ciudadMapper;
        this.centromedicoMapper = centromedicoMapper;
    }

    @Override
    public Flux<CentroMedico> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<CentroMedico> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = CentroMedicoSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CiudadSqlHelper.getColumns(ciudadTable, "ciudad"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(ciudadTable)
            .on(Column.create("ciudad_id", entityTable))
            .equals(Column.create("id", ciudadTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, CentroMedico.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<CentroMedico> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<CentroMedico> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<CentroMedico> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<CentroMedico> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<CentroMedico> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private CentroMedico process(Row row, RowMetadata metadata) {
        CentroMedico entity = centromedicoMapper.apply(row, "e");
        entity.setCiudad(ciudadMapper.apply(row, "ciudad"));
        return entity;
    }

    @Override
    public <S extends CentroMedico> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
