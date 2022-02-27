package org.example.sdj.advance.support;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.dialect.Dialect;

import java.util.List;
import java.util.stream.Collectors;

public class BaseSqlGenerator {

    Dialect dialect;

    public BaseSqlGenerator(Dialect dialect) {
        this.dialect = dialect;
    }

    public String getPageableSql(Pageable pageable) {
        String sql = " ";
        if (pageable != null && pageable.isPaged()) {
            if (pageable.getPageNumber() == 0) {
                sql += dialect.limit().getLimit(pageable.getPageSize());
            } else {
                sql += dialect.limit().getLimitOffset(pageable.getOffset(), pageable.getPageSize());
            }
        }
        return sql;
    }

    public String getSortSql(Sort sort) {
        if (sort != null && sort.isSorted()) {
            List<String> orders = sort.stream()
                    .map(order -> order.getDirection() == null ? order.getProperty() : order.getProperty() + " " + order.getDirection())
                    .collect(Collectors.toList());
            return "ORDER BY " + StringUtils.join(orders, ",");
        }
        return "";
    }

}
