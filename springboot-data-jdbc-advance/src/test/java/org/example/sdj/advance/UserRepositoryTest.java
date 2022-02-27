package org.example.sdj.advance;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.IteratorUtils;
import org.example.sdj.advance.dao.UserAddressRepository;
import org.example.sdj.advance.dao.UserInfoRepository;
import org.example.sdj.advance.dto.UserAddressDTO;
import org.example.sdj.advance.entity.UserInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@SpringBootTest(classes = SdjAdvApplication.class)
@Slf4j
@Transactional
@Rollback(false)
public class UserRepositoryTest {

    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    UserAddressRepository userAddressRepository;

    @Test
    public void testFindAll() {
        List<UserInfo> users = IteratorUtils.toList(userInfoRepository.findAll(Pageable.ofSize(5)).iterator());
        log.info("users="+users);
    }

    @Test
    void tesstCrud() {
        List<UserInfo> users = IteratorUtils.toList(userInfoRepository.findAll().iterator());

        Date now = new Date();
        UserInfo user0 = users.get(0);
        user0.setUpdateTime(now);
        userInfoRepository.save(user0);
        UserInfo userAfterUpdated = userInfoRepository.findById(user0.getId()).get();
        Assertions.assertEquals(now, userAfterUpdated.getUpdateTime());

        userInfoRepository.deleteById(users.get(1).getId());
        List<UserInfo> usersAfterdeleted = IteratorUtils.toList(userInfoRepository.findAll().iterator());
        Assertions.assertEquals(users.size() - 1, usersAfterdeleted.size());

    }


    @Test
    void testFindAddressWithLoginName(){
        List<UserAddressDTO> addressDTOS = userAddressRepository.findUserAddressWithLoginName();
        Assertions.assertNotNull(addressDTOS.get(0).getLoginName());
        log.info("addressDTOS="+addressDTOS);
    }

}

