package org.example.sdj.mutidb.dborder.dao;

import org.example.sdj.mutidb.dborder.entity.OrderItem;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * OrderItem Repository
 *
 * @author auto generated
 * @date 2022-02-22 21:59:01
 */
public interface OrderItemRepository extends PagingAndSortingRepository<OrderItem, Long> ,OrderItemRepositoryCustom{


    List<OrderItem> findAllByOrderSn(String orderSn);

    @Modifying
    @Query("delete from \"order_item\" where ORDER_SN=(select ORDER_SN from \"order_info\" where ID=:ID) ")
    int deleteByOrderId(Long id);

}
