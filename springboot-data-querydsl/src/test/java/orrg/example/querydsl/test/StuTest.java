package orrg.example.querydsl.test;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BeanPath;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.sql.SQLQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.example.querydsl.QueryDslApp;
import org.example.querydsl.StudentRepository;
import org.example.querydsl.entity.QScore;
import org.example.querydsl.entity.QStudent;
import org.example.querydsl.entity.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.relational.core.sql.Expressions;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest(classes = QueryDslApp.class)
@ExtendWith(SpringExtension.class)
@Transactional
@Slf4j
public class StuTest {

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

    @Test
    public void testQueryStuAndScore() {
        QStudent student = QStudent.student;
        QScore score = QScore.score;
        Predicate condition = QStudent.student.age.gt(20)
                .and(student.gender.eq("male"))
                ;

        QueryResults<Tuple> res = queryFactory.select(student,score)
                .from(student)
                .innerJoin(score).on(student.id.eq(score.stuId))
                .where(condition)
                .orderBy(student.id.desc())
                .fetchResults();
        log.info("res"+res);
    }


}
