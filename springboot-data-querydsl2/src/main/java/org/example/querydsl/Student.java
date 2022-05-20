package org.example.querydsl;

import com.infobip.spring.data.jdbc.annotation.processor.Schema;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.annotation.Generated;

/**
 * Student is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
@Table("STUDENT")
@Data
@ToString
public class Student {

    private Integer age;

    private String gender;

    @Id
    private Integer id;

    private String name;
}

