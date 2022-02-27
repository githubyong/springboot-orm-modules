package org.example.sdj.advance.support;

import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PersistentEntityCache {

    private static Map<Class, RelationalPersistentEntity> entityMap = new ConcurrentHashMap<>();

    static JdbcMappingContext jdbcMappingContext = new JdbcMappingContext();

    public static void putEntity(Class clz, PersistentEntity entity) {
        if (entity instanceof RelationalPersistentEntity) {
            entityMap.put(clz, (RelationalPersistentEntity) entity);
        }
    }

    public static Optional<RelationalPersistentEntity> getEntity(Class<?> clz) {
        if (!entityMap.containsKey(clz)) {
            entityMap.put(clz, jdbcMappingContext.getPersistentEntity(clz));
        }
        return Optional.ofNullable(entityMap.get(clz));
    }

}
