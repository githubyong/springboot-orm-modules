package org.example.sjpa;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = {"org.example.sjpa"})
@EnableTransactionManagement    // 启用事务管理器
public class DbConfig {

}
