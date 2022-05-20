package org.example.querydsl;

import com.google.common.base.CaseFormat;
import com.infobip.spring.data.jdbc.annotation.processor.ProjectTableCaseFormat;
import com.infobip.spring.data.jdbc.annotation.processor.Schema;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.annotation.Generated;

/**
 * Score is a Querydsl bean type
 */


@Data
@ToString
@Table("SCORE")
@ProjectTableCaseFormat(CaseFormat.LOWER_UNDERSCORE)
@Generated("com.querydsl.codegen.BeanSerializer")
public class Score {

    private String courseName;

    @Id
    private Integer id;

    private java.math.BigDecimal scorePoint;

    private Integer stuId;

    private String year;
}

