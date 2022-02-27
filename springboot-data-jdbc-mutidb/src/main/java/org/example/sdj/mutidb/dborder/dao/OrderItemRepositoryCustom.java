package org.example.sdj.mutidb.dborder.dao;

import org.example.sdj.mutidb.dborder.entity.OrderItem;

import java.util.List;

public interface OrderItemRepositoryCustom {

    List<OrderItem> findByOrderId(Long orderId);
}
