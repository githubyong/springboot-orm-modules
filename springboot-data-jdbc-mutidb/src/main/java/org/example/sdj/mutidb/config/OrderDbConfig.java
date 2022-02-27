package org.example.sdj.mutidb.config;

import org.example.sdj.mutidb.JdbcCustomConversionsUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.convert.*;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.DialectResolver;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;

@Conditional(DBConfigConditional.class)
@Configuration
@EnableJdbcRepositories( basePackages = "org.example.sdj.mutidb.dborder",
        jdbcOperationsRef = "orderdbJdbcOperations",
        transactionManagerRef = "orderdbTransactionManager",
        dataAccessStrategyRef = "orderdbDataAccessStrategy"
)
@EnableAutoConfiguration(
        exclude = {DataSourceAutoConfiguration.class, JdbcRepositoriesAutoConfiguration.class}
)
@ConfigurationPropertiesScan
public class OrderDbConfig extends AbstractJdbcConfiguration{


    @Bean("orderdbDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.orderdb")
    public DataSource orderdbDataSource() {
        return DataSourceBuilder.create().build();
    }


    @Bean("orderdbJdbcOperations")
    NamedParameterJdbcOperations orderdbJdbcOperations(@Qualifier("orderdbDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean(name = "orderdbJdbcTemplate")
    public JdbcTemplate orderdbJdbcTemplate(@Qualifier("orderdbDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean("orderdbTransactionManager")
    public TransactionManager orderdbTransactionManager(@Qualifier("orderdbDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    @Qualifier("orderdbDataAccessStrategy")
    public DataAccessStrategy orderdbDataAccessStrategy(@Qualifier("orderdbJdbcOperations") NamedParameterJdbcOperations operations, JdbcConverter jdbcConverter, JdbcMappingContext context) {
        return new DefaultDataAccessStrategy(new SqlGeneratorSource(context, jdbcConverter, DialectResolver.getDialect(operations.getJdbcOperations())), context, jdbcConverter, operations);
    }

    @Override
    public JdbcCustomConversions jdbcCustomConversions() {
        return JdbcCustomConversionsUtil.jdbcCustomConversions();
    }

}