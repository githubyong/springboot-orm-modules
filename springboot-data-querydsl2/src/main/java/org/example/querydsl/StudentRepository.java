package org.example.querydsl;

import com.infobip.spring.data.jdbc.QuerydslJdbcFragment;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StudentRepository extends PagingAndSortingRepository<Student, Integer>, QuerydslPredicateExecutor<Student>, QuerydslJdbcFragment<Student> {

}
