package org.example.sdj.min;

import org.apache.commons.collections.IteratorUtils;
import org.example.sdj.min.dao.UserInfoRepository;
import org.example.sdj.min.entity.UserInfo;
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
