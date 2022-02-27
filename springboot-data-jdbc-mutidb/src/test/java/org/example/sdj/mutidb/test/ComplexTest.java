package org.example.sdj.mutidb.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.IteratorUtils;
import org.example.sdj.mutidb.SdjMutidbApplication;
import org.example.sdj.mutidb.dborder.dao.OrderInfoRepository;
import org.example.sdj.mutidb.dborder.dao.OrderItemRepository;
import org.example.sdj.mutidb.dborder.entity.OrderInfo;
import org.example.sdj.mutidb.dborder.entity.OrderItem;
import org.example.sdj.mutidb.dbuser.dao.UserInfoRepository;
import org.example.sdj.mutidb.dbuser.entity.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@SpringBootTest(classes = SdjMutidbApplication.class)
@Slf4j
public class ComplexTest {

    @Autowired
    OrderInfoRepository orderInfoRepository;

    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    OrderItemRepository orderItemRepository;


    @Test
    void testFindMutiDb(){
        List<UserInfo> users = IteratorUtils.toList(userInfoRepository.findAll(Pageable.ofSize(5)).iterator());
        UserInfo userInfo = users.get(0);
        List<OrderInfo> orderInfosOfUser =orderInfoRepository.findAllByMemberUsername(userInfo.getLoginName());
        List<OrderItem> items = IteratorUtils.toList(orderItemRepository.findAll().iterator());
        log.info("orderInfosOfUser="+orderInfosOfUser);
    }
}
