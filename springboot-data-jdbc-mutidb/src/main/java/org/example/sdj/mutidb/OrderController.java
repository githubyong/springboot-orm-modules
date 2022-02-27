package org.example.sdj.mutidb;

import org.apache.commons.collections.IteratorUtils;
import org.example.sdj.mutidb.dborder.dao.OrderInfoRepository;
import org.example.sdj.mutidb.dborder.dao.OrderItemRepository;
import org.example.sdj.mutidb.dborder.entity.OrderInfo;
import org.example.sdj.mutidb.dborder.entity.OrderItem;
import org.example.sdj.mutidb.dbuser.dao.UserInfoRepository;
import org.example.sdj.mutidb.dbuser.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {


    @Autowired
    OrderService orderService;

    @GetMapping("/list")
    public List<OrderInfo> findAll(){
        return orderService.findAllOrders();
    }

    @GetMapping("/{id}")
    public OrderInfo orderInfo(@PathVariable("id") Long id){
        return orderService.findOrderInfo(id);
    }

    @GetMapping("/{sn}/details")
    public List<OrderItem> orderItems(@PathVariable("sn") String sn){
        return orderService.findAllByOrderSn(sn);
    }





    @PostMapping()
    public OrderInfo saveUser(@RequestBody OrderInfo stu) {
        return orderService.saveOrderInfo(stu);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        orderService.deleteOrder(id);
    }
}
