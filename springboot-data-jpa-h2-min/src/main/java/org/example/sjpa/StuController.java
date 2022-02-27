package org.example.sjpa;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stus")
public class StuController {

    @Autowired
    StuRepository stuRepository;

    @PostMapping()
    public Stu saveUser(@RequestBody Stu stu) {
        return stuRepository.save(stu);
    }

    @GetMapping("/list")
    public List<Stu> findAll(){
        return stuRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Integer id) {
        stuRepository.deleteById(id);
    }

}
