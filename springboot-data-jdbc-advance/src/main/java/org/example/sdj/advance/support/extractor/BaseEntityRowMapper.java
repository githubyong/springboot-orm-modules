package org.example.sdj.advance.support.extractor;

import lombok.SneakyThrows;
import org.example.sdj.advance.support.ResultReadUtil;
import org.example.sdj.advance.support.ResultRowLine;
import org.example.sdj.advance.support.entity.BaseEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseEntityRowMapper<T extends BaseEntity> implements RowMapper<T> {

    Class<T> domainType;
    Map<String, String> tableAlias;


    public BaseEntityRowMapper(Class<T> domainType, @Nullable Map<String, String> tableAlias) {
        this.domainType = domainType;
        this.tableAlias = tableAlias;
    }

    @SneakyThrows
    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResultRowLine rowLine = ResultReadUtil.readResultRowLine(rs);
        T row = ResultReadUtil.readEntity(rowLine, domainType, tableAlias);
        List<Class> usedClass = new ArrayList<>();
        usedClass.add(domainType);
        ResultReadUtil.extendRow(rowLine, row, usedClass, tableAlias);
        return row;
    }
}
