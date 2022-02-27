package org.example.sdj.mutidb.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.IteratorUtils;
import org.example.sdj.mutidb.SdjMutidbApplication;
import org.example.sdj.mutidb.dborder.dao.OrderInfoRepository;
import org.example.sdj.mutidb.dborder.dao.OrderItemRepository;
import org.example.sdj.mutidb.dborder.entity.OrderInfo;
import org.example.sdj.mutidb.dborder.entity.OrderItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


@SpringBootTest(classes = SdjMutidbApplication.class)
@Slf4j
public class OrderRepositoryTest {

    @Autowired
    OrderInfoRepository orderInfoRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Test
    void testFindAllOrders(){
        Page orderInfos = orderInfoRepository.findAll(Pageable.ofSize(5));
        log.info("orderInfos="+orderInfos);
    }

    @Test
    void testFindOrderItems(){
        List<OrderItem> items = orderItemRepository.findByOrderId(1L);
        log.info("items="+items);
    }
}
