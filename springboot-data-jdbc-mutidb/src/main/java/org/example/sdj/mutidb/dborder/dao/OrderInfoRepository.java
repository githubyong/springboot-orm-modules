package org.example.sdj.mutidb.dborder.dao;

import org.example.sdj.mutidb.dborder.entity.OrderInfo;
import org.example.sdj.mutidb.dbuser.entity.UserInfo;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * OrderInfo Repository
 *
 * @author auto generated
 * @date 2022-02-22 21:59:01
 */
public interface OrderInfoRepository extends PagingAndSortingRepository<OrderInfo, Long> {

    List<OrderInfo> findAllByMemberUsername(String userName);
}
