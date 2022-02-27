package org.example.sdj.advance.support.entity;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
@Inherited
public @interface CustomField {

    /**
     * findByEntity时 query operator
     *
     * @return
     */
    QueryOperator qeuryOperator() default QueryOperator.EQ;

    /**
     * findByEntity时 如果此属性为true,拼接查询sql时不判断是否非空
     *
     * @return
     */
    boolean qeuryEmpty() default true;

    /**
     * updateEntity时，如果此属性为true,拼接更新sql时不判断是否非空
     *
     * @return
     */
    boolean updateEmpty() default true;

    /**
     * 关联查询读取查询结果时，引用其它表的属性设置为自身属性
     * @return
     */
    String refValue() default "";

    /**
     * updateEntity时，如果此属性为true,不拼接更新sql
     * @return
     */
    boolean readOnlyField() default false;

    enum QueryOperator {
        EQ, LIKE;
    }
}
