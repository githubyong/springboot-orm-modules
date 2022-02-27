package org.example.sdj.advance.dao;

import org.example.sdj.advance.dto.OrderItemDTO;
import org.example.sdj.advance.entity.OrderItem;
import org.example.sdj.advance.support.MyJdbcRepository;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * OrderItem Repository
 *
 * @author auto generated
 * @date 2022-02-26 22:54:58
 */
public interface OrderItemRepository extends MyJdbcRepository<OrderItem, Long> {

    @Query(value = "select * from \"order_item\" inner join \"order_info\" on(\"order_item\".order_sn=\"order_info\".order_sn)",resultSetExtractorRef ="OrderItemDTOResultSetExtractor" )
    List<OrderItemDTO> findOrderItems();
}
