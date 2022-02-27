package org.example.sdj.advance;

import lombok.extern.slf4j.Slf4j;
import org.example.sdj.advance.dao.OrderInfoRepository;
import org.example.sdj.advance.dao.OrderItemRepository;
import org.example.sdj.advance.dto.OrderInfoDTO;
import org.example.sdj.advance.dto.OrderItemDTO;
import org.example.sdj.advance.entity.OrderInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@SpringBootTest(classes = SdjAdvApplication.class)
@Slf4j
@Transactional
@Rollback(false)
public class OrderRepositoryTest {

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    OrderInfoRepository orderInfoRepository;


    @Test
    void teindFindOrderItemsWtithOrder(){
        List<OrderItemDTO> items = orderItemRepository.findOrderItems();
        OrderInfo orderInfo = items.get(0).getOrderInfo();
        Assertions.assertNotNull(orderInfo);
        log.info("items="+items);
    }

    @Test
    void testFindOrderWithItems(){
        List<OrderInfoDTO> orders = orderInfoRepository.findOrdersAndItems();
        Assertions.assertNotNull(orders.get(0).getOrderItems());
        log.info("orders="+orders);
    }

    @Test
    void testFindByEntity(){
        OrderInfo query = OrderInfo.builder().payType(0).build();
        List<OrderInfo> orders = orderInfoRepository.findByEntity(query);
        log.info("orders="+orders);
    }

    @Test
    void testUpdateByEntity(){
        OrderInfo updateObj = OrderInfo.builder().id(1L).payType(0).build();
        int row =  orderInfoRepository.updateSelective(updateObj);
        Assertions.assertNotNull(row);
    }

    @Test
    void testInsertEntity(){
        OrderInfo insertObj = OrderInfo.builder().memberUsername("t03")
                .totalAmount(BigDecimal.valueOf(200)).orderType(0).payType(0)
                .createTime(new Date()).orderSn("TEST_00001").build();
        OrderInfo info = orderInfoRepository.insertSelective(insertObj);
        log.info("inserted="+info);
    }
}
