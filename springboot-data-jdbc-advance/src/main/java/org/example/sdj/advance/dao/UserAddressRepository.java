package org.example.sdj.advance.dao;

import org.example.sdj.advance.dto.UserAddressDTO;
import org.example.sdj.advance.entity.UserAddress;
import org.example.sdj.advance.support.MyJdbcRepository;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * UserAddress Repository
 *
 * @author auto generated
 * @date 2022-02-26 22:54:58
 */
public interface UserAddressRepository extends MyJdbcRepository<UserAddress, Long> {

    @Query(value = "select \"user_address\".*,login_name from \"user_address\" " +
            "inner join  \"user_info\" on(\"user_address\".user_id=\"user_info\".id)",resultSetExtractorRef ="UserAddressDTOResultSetExtractor" )
    List<UserAddressDTO> findUserAddressWithLoginName();

}
