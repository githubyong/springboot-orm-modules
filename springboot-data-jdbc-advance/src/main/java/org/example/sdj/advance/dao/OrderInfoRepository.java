package org.example.sdj.advance.dao;

import org.example.sdj.advance.dto.OrderInfoDTO;
import org.example.sdj.advance.entity.OrderInfo;
import org.example.sdj.advance.support.MyJdbcRepository;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * OrderInfo Repository
 *
 * @author auto generated
 * @date 2022-02-26 22:54:58
 */
public interface OrderInfoRepository extends MyJdbcRepository<OrderInfo, Long> {

    @Query(value = "select * from \"order_info\" left join \"order_item\" on(\"order_item\".order_sn=\"order_info\".order_sn)",resultSetExtractorRef ="OrderInfoDTOResultSetExtractor" )
    List<OrderInfoDTO> findOrdersAndItems();
}
