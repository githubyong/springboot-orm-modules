package org.example.sdj.mutidb.dborder.dao;

import org.example.sdj.mutidb.dborder.entity.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.util.List;

public class OrderItemRepositoryCustomImpl implements OrderItemRepositoryCustom {

    @Qualifier("orderdbJdbcOperations")
    @Autowired
    NamedParameterJdbcOperations jdbcOperations;


    @Override
    public List<OrderItem> findByOrderId(Long orderId) {
        String sql = "select \"order_item\".* from \"order_item\" inner join \"order_info\" on(\"order_info\".ORDER_SN=\"order_item\".ORDER_SN) where \"order_info\".ID=:ID";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("ID", orderId);

        return jdbcOperations.query(sql, parameterSource, new BeanPropertyRowMapper<>(OrderItem.class));
    }
}
