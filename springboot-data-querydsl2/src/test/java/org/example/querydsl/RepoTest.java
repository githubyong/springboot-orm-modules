package org.example.querydsl;

import com.querydsl.core.types.Predicate;
import com.querydsl.sql.SQLQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest(classes = QueryDslApp.class)
@ExtendWith(SpringExtension.class)
@Transactional
@Slf4j
public class RepoTest {

    @Autowired
    private SQLQueryFactory queryFactory;

    @Autowired
    StudentRepository studentRepository;

    @Test
    public void testQueryStu() {
        QStudent student = QStudent.student;
        Predicate condition = student.age.gt(20)
                .and(student.gender.eq("male"));

        List<Student> stus = queryFactory.selectFrom(student)
                .where(condition)
                .orderBy(student.id.desc())
                .fetch();
        log.info("res"+stus);
    }
}
