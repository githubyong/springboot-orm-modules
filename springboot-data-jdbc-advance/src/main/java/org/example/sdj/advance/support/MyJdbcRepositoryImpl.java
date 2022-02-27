package org.example.sdj.advance.support;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.repository.support.SimpleJdbcRepository;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public class MyJdbcRepositoryImpl<T, ID> extends SimpleJdbcRepository<T, ID> implements MyJdbcRepository<T, ID> {

    protected BaseDao baseDao;

    public MyJdbcRepositoryImpl(JdbcAggregateOperations entityOperations, PersistentEntity entity, NamedParameterJdbcOperations jdbcOperations, Dialect dialect, JdbcConverter converter) {
        super(entityOperations, entity);
        PersistentEntityCache.putEntity(entity.getType(), entity);
        this.baseDao = new BaseDao(jdbcOperations, dialect, entity.getType(), converter);
    }

    @Override
    public List<T> findByEntity(T t) {
        return baseDao.findByEntity(t, null);
    }

    @Override
    public List<T> findByEntity(T t, Pageable pageable) {
        return baseDao.findByEntity(t, pageable);
    }

    @Override
    @Transactional
    public int updateSelective(T t) {
        return baseDao.updateEntitySelective(t);
    }

    @Override
    @Transactional
    public T insertSelective(T t) {
        return (T) baseDao.insertEntitySelective(t);
    }

    @Override
    @Transactional
    public int updateById(ID id, Map<String, Object> paraMap) {
        return baseDao.updateEntity(id, paraMap);
    }
}
