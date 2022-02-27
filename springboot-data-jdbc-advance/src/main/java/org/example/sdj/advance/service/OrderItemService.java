package org.example.sdj.advance.service;

import org.example.sdj.advance.dao.OrderItemRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * OrderItem service层
 *
 * @author auto generated
 * @date 2022-02-26 22:54:58
 */
@Service
public class OrderItemService {

	@Resource
	private OrderItemRepository orderItemRepository;
}
