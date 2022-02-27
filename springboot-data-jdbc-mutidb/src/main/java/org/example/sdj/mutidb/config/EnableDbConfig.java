package org.example.sdj.mutidb.config;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
@AutoConfigureOrder(Integer.MAX_VALUE)
public @interface EnableDbConfig {

    Class<?>[] value() default {};
}