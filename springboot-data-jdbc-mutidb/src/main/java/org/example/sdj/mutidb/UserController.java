package org.example.sdj.mutidb;

import org.apache.commons.collections.IteratorUtils;
import org.example.sdj.mutidb.dborder.dao.OrderInfoRepository;
import org.example.sdj.mutidb.dbuser.dao.UserInfoRepository;
import org.example.sdj.mutidb.dbuser.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserInfoRepository userInfoRepository;

    @GetMapping("/list")
    public List<UserInfo> findAll(){
       return IteratorUtils.toList(userInfoRepository.findAll().iterator());
    }


    @PostMapping()
    public UserInfo saveUser(@RequestBody UserInfo stu) {
        return userInfoRepository.save(stu);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        userInfoRepository.deleteById(id);
    }
}
