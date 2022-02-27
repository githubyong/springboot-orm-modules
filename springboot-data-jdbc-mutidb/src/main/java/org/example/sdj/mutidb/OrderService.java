package org.example.sdj.mutidb;

import org.apache.commons.collections.IteratorUtils;
import org.example.sdj.mutidb.dborder.dao.OrderInfoRepository;
import org.example.sdj.mutidb.dborder.dao.OrderItemRepository;
import org.example.sdj.mutidb.dborder.entity.OrderInfo;
import org.example.sdj.mutidb.dborder.entity.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    OrderInfoRepository orderInfoRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    public List<OrderInfo> findAllOrders(){
        return IteratorUtils.toList(orderInfoRepository.findAll().iterator());    }

    public OrderInfo findOrderInfo(Long id) {
        return orderInfoRepository.findById(id).get();
    }

    public List<OrderItem> findAllByOrderSn(String sn) {
        return orderItemRepository.findAllByOrderSn(sn);
    }


    public OrderInfo saveOrderInfo(OrderInfo info) {
        return orderInfoRepository.save(info);
    }

    @Transactional("orderdbTransactionManager")
    public void deleteOrder(Long id) {
        orderItemRepository.deleteByOrderId(id);
        orderInfoRepository.deleteById(id);
    }
}
