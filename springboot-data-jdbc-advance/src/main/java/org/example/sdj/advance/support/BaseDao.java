package org.example.sdj.advance.support;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.sdj.advance.support.entity.CustomField;
import org.example.sdj.advance.support.entity.CustomField.QueryOperator;
import org.example.sdj.advance.support.entity.EntityUtil;
import org.example.sdj.advance.support.extractor.BaseEntityResultSetExtractor;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.convert.EntityRowMapper;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.dialect.RenderContextFactory;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.sql.*;
import org.springframework.data.relational.core.sql.render.SqlRenderer;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
public class BaseDao<T> {

    NamedParameterJdbcOperations jdbcOperations;

    BaseSqlGenerator sqlGenerator;

    JdbcConverter converter;

    Class domainType;

    private RenderContextFactory renderCxtFactory;


    public BaseDao(NamedParameterJdbcOperations jdbcOperations, Dialect dialect, Class clz, JdbcConverter converter) {
        this.jdbcOperations = jdbcOperations;
        this.domainType = clz;
        this.sqlGenerator = new BaseSqlGenerator(dialect);
        this.converter = converter;
        renderCxtFactory = new RenderContextFactory(dialect);
    }


    private EntityRowMapper<T> defaultEntityRowMapper() {
        Optional<RelationalPersistentEntity> entity = PersistentEntityCache.getEntity(domainType);
        return new EntityRowMapper(entity.get(), converter);
    }

    public List<T> findAllBySQL(String sql, @Nullable Pageable pageable) {
        return this.findAllBySQL(sql, null, pageable, null);
    }

    public List<T> findAllBySQL(String sql, @Nullable Map<String, ?> paramMap, @Nullable Pageable pageable) {
        return this.findAllBySQL(sql, paramMap, pageable, null);
    }

    public List<T> findAllBySQL(String sql, @Nullable Map<String, ?> paramMap, @Nullable Pageable pageable, @Nullable RowMapper rowMapper) {
        SqlParameterSource paramSource = MapUtils.isEmpty(paramMap) ? EmptySqlParameterSource.INSTANCE : new MapSqlParameterSource(paramMap);
        String whole_sql = sql;
        if (pageable != null && !StringUtils.containsIgnoreCase(sql, "ORDER BY")) {
            whole_sql += sqlGenerator.getSortSql(pageable.getSort());
        }
        whole_sql += sql + sqlGenerator.getPageableSql(pageable);
        rowMapper = rowMapper == null ? defaultEntityRowMapper() : rowMapper;
        return jdbcOperations.query(whole_sql, paramSource, rowMapper);
    }

    public List<T> findByMapSelective(String sql, Map<String, Object> paraMap, @Nullable Pageable pageable) {
        return this.findByMapSelective(sql, paraMap, pageable, null);
    }

    public List<T> findByMapSelective(String sql, Map<String, Object> paraMap, @Nullable Pageable pageable, @Nullable EntityRowMapper<T> rowMapper) {
        StringBuilder lastSql = new StringBuilder(sql);
        Map<String, Object> lastMap = new HashMap();
        for (Map.Entry<String, Object> entry : paraMap.entrySet()) {
            if (EntityUtil.isNotEmptyObj(entry.getValue())) {
                lastSql.append(String.format(" AND %s=:%s", entry.getKey()));
                lastMap.put(entry.getKey(), entry.getValue());
            }
        }
        lastSql.append(sqlGenerator.getPageableSql(pageable));
        rowMapper = rowMapper == null ? defaultEntityRowMapper() : rowMapper;
        return jdbcOperations.query(lastSql.toString(), paraMap, rowMapper);
    }

    public List<T> findByMapSelectiveCustom(String sql, Map<String, Object> paraMap, @Nullable Pageable pageable, @Nullable ResultSetExtractor<T> resultSetExtractor) {
        StringBuilder lastSql = new StringBuilder(sql);
        Map<String, Object> lastMap = new HashMap();
        for (Map.Entry<String, Object> entry : paraMap.entrySet()) {
            if (EntityUtil.isNotEmptyObj(entry.getValue())) {
                lastSql.append(String.format(" AND %s=:%s", entry.getKey()));
                lastMap.put(entry.getKey(), entry.getValue());
            }
        }
        lastSql.append(sqlGenerator.getPageableSql(pageable));
        resultSetExtractor = resultSetExtractor == null ? new BaseEntityResultSetExtractor<>(domainType) : resultSetExtractor;
        return (List<T>) jdbcOperations.query(lastSql.toString(), paraMap, resultSetExtractor);
    }

    public <T> List<T> findByEntity(@NonNull T t, @Nullable Pageable pageable) {
        Class domainClz = t.getClass();

        Condition predicate = TrueCondition.INSTANCE;
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        for (Field field : domainClz.getDeclaredFields()) {
            Column columnAnnotation = field.getAnnotation(Column.class);
            CustomField customField = field.getAnnotation(CustomField.class);
            String column = columnAnnotation.value();
            Object fieldVal = EntityUtil.getProperty(t, field);
            QueryOperator operator = customField == null ? QueryOperator.EQ : customField.qeuryOperator();
            boolean queryExpty = customField == null ? false : customField.qeuryEmpty();
            if (queryExpty || fieldVal != null) {
                if (QueryOperator.LIKE.equals(operator)) {
                    predicate = predicate.and(Conditions.just(String.format("%s like '%:%s'", column, column)));
                    paramSource.addValue(column, fieldVal + "*");
                } else if (QueryOperator.EQ.equals(operator)) {
                    predicate = predicate.and(Conditions.just(String.format("%s=:%s", column, column)));
                    paramSource.addValue(column, fieldVal);
                }
            }
        }

        RelationalPersistentEntity entity = PersistentEntityCache.getEntity(domainClz).get();
        Table table = Table.create(entity.getTableName());

        long limit = pageable != null && pageable.isPaged() ? pageable.getPageSize() : -1;
        long offset = pageable != null && pageable.isPaged() ? pageable.getOffset() : -1;

        Select select = StatementBuilder.select(table.asterisk())
                .from(table)
                .limitOffset(limit, offset)
                .where(predicate)
                .build();
        String sql = SqlRenderer.create(renderCxtFactory.createRenderContext()).render(select);
        log.debug("findByEntity_sql ={},paraMap={}", sql, paramSource.getValues());
        return (List<T>) jdbcOperations.query(sql, paramSource, defaultEntityRowMapper());
    }

    public int updateEntitySelective(@NonNull T t) {
        Class domainClz = t.getClass();
        RelationalPersistentEntity entity = PersistentEntityCache.getEntity(domainClz).get();
        Table table = Table.create(entity.getTableName());

        List<Assignment> assignments = new ArrayList<>();
        Map<String, Object> paraMap = new HashMap<>();

        entity.doWithAll(property -> {
            Field field = property.getField();
            boolean ignore = property.isTransient() || property.isIdProperty() || property.isEntity()
                    || field.getAnnotation(ReadOnlyProperty.class) != null;

            if(!ignore){
                String column = field.getAnnotation(Column.class).value();
                Object fieldVal = EntityUtil.getProperty(t, field);
                CustomField customField = field.getAnnotation(CustomField.class);
                boolean updateEmpty = customField == null ? false : customField.updateEmpty();
                boolean readOnly =  customField != null && customField.readOnlyField();
                if (!readOnly && (updateEmpty || fieldVal!= null)) {
                    assignments.add(Assignments.value(table.column(column), Expressions.just(":" + column)));
                    paraMap.put(column, fieldVal);
                }
            }
        });

        String pkColumn = entity.getIdColumn().getReference();
        Condition predicate = Conditions.just(String.format("%s=:%s", pkColumn, pkColumn));
        paraMap.put(pkColumn, EntityUtil.getPKID(t));

        Update update = Update.builder()
                .table(table)
                .set(assignments)
                .where(predicate)
                .build();

        String sql = SqlRenderer.create(renderCxtFactory.createRenderContext()).render(update);
        log.debug("updateEntitySelective_sql ={},paraMap={}", sql, paraMap);
        return jdbcOperations.update(sql, paraMap);
    }

    public T insertEntitySelective(@NonNull T t) {
        Class domainClz = t.getClass();
        RelationalPersistentEntity entity = PersistentEntityCache.getEntity(domainClz).get();
        Table table = Table.create(entity.getTableName());

        List<Assignment> assignments = new ArrayList<>();
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        InsertBuilder.InsertIntoColumnsAndValuesWithBuild insert = Insert.builder().into(table);


        entity.doWithAll(property -> {
            Field field = property.getField();
            boolean ignore = property.isTransient() || property.isIdProperty() || property.isEntity()
                    || field.getAnnotation(ReadOnlyProperty.class) != null;

            if(!ignore){
                String column = field.getAnnotation(Column.class).value();
                Object fieldVal = EntityUtil.getProperty(t, field);
                CustomField customField = field.getAnnotation(CustomField.class);
                boolean updateEmpty = customField == null ? false : customField.updateEmpty();
                boolean readOnly =  customField != null && customField.readOnlyField();
                if (!readOnly && (updateEmpty || fieldVal!= null)) {
                    assignments.add(Assignments.value(table.column(column), Expressions.just(":" + column)));
                    insert.column(table.column(column));
                    insert.value(Expressions.just(":" + column));
                    parameterSource.addValue(column,fieldVal);
                }
            }
        });

        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = SqlRenderer.create(renderCxtFactory.createRenderContext()).render(insert.build());
        log.debug("insertEntitySelective_sql ={},paraMap={}", sql, parameterSource);
        jdbcOperations.update(sql, parameterSource, keyHolder);
        if(!EntityUtil.isNotEmptyObj(EntityUtil.getPKID(t))){
            EntityUtil.setPKID(t,keyHolder.getKey());
        }
        return t;
    }

    public int updateEntity(Object id, Map<String, Object> paraMap) {
        Map<String, Object> map = new HashMap<>(paraMap);
        String tableName = EntityUtil.tableName(domainType);
        Table table = SQL.table(tableName);
        List<Assignment> assignments = new ArrayList<>();

        for (Map.Entry<String, Object> entry : paraMap.entrySet()) {
            assignments.add(Assignments.value(table.column(entry.getKey()), Expressions.just(":" + entry.getKey())));
        }

        String pkColumn = EntityUtil.getPKColumn(domainType);
        Condition predicate = Conditions.just(String.format("%s=:%s", pkColumn, pkColumn));
        map.put(pkColumn, id);

        Update update = Update.builder()
                .table(table)
                .set(assignments)
                .where(predicate)
                .build();

        String sql = SqlRenderer.create(renderCxtFactory.createRenderContext()).render(update);
        log.debug("updateEntity_sql ={},paraMap={}", sql, paraMap);
        return jdbcOperations.update(sql, map);
    }


}
