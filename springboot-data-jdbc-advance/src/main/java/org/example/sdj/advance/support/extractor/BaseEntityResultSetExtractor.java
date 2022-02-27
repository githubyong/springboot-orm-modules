package org.example.sdj.advance.support.extractor;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.sdj.advance.support.ResultReadUtil;
import org.example.sdj.advance.support.ResultRowLine;
import org.example.sdj.advance.support.entity.BaseEntity;
import org.example.sdj.advance.support.entity.EntityUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.lang.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@Slf4j
public class BaseEntityResultSetExtractor<T extends BaseEntity> implements ResultSetExtractor<List<T>> {

    Class<T> domainType;
    Map<String, String> propsMapping;

    public BaseEntityResultSetExtractor(Class<T> domainType, @Nullable Map<String, String> propsMapping) {
        this.domainType = domainType;
        this.propsMapping = propsMapping == null ? new HashMap<>() : propsMapping;
    }

    public BaseEntityResultSetExtractor(Class<T> domainType) {
        this(domainType, null);
    }

    @SneakyThrows
    @Override
    public List<T> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<ResultRowLine> resultRowLines = ResultReadUtil.readResultRowLines(rs);
        Map<Object, T> rowMap = new LinkedHashMap<>();
        for (ResultRowLine resultRowLine : resultRowLines) {
            Object id = EntityUtil.getPKID(domainType, resultRowLine);
            if (!rowMap.containsKey(id)) {
                rowMap.put(id, ResultReadUtil.readEntity(resultRowLine, domainType, propsMapping));
            }
            Object row = rowMap.get(id);
            List<Class> usedClass = new ArrayList<>();
            usedClass.add(domainType);
            ResultReadUtil.extendRow(resultRowLine, row, usedClass, null);
        }
        return Lists.newArrayList(rowMap.values());
    }
}
