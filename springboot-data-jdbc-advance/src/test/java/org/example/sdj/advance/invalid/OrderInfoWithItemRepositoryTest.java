package org.example.sdj.advance.invalid;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.IteratorUtils;
import org.example.sdj.advance.SdjAdvApplication;
import org.example.sdj.advance.dao.OrderItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest(classes = SdjAdvApplication.class)
@Slf4j
public class OrderInfoWithItemRepositoryTest {

    @Autowired
    OrderInfoWithItemRepository orderInfoWithItemRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Test
    void testFindAll(){
        List<OrderInfoWithItems> list = IteratorUtils.toList(orderInfoWithItemRepository.findAll().iterator());
        log.info("list="+list);
    }
}
