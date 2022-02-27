package org.example.sjpa.test;

import lombok.extern.slf4j.Slf4j;
import org.example.sjpa.SJpaApplication;
import org.example.sjpa.Stu;
import org.example.sjpa.StuRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional
@Rollback(false)
@SpringBootTest(classes = SJpaApplication.class)
public class StuRepositoryTest {

    @Autowired
    StuRepository stuRepository;

    @Test
    public void testFindAll() {
        List<Stu> stus = stuRepository.findAll();
        Assertions.assertTrue(stus.size() == 5);
    }
}
