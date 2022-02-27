package org.example.sdj.advance.support;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Map;

@NoRepositoryBean
public interface MyJdbcRepository<T, ID> extends PagingAndSortingRepository<T,ID> {

    List<T> findByEntity(T t);

    List<T> findByEntity(T t, Pageable pageable);

    int updateSelective(T t);

    T insertSelective(T t);

    int updateById(ID id, Map<String,Object> paraMap);
}
