package org.example.sdj.min;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import javax.sql.DataSource;

@Configuration
@EnableJdbcRepositories(basePackages = "org.example.sdj")
@EnableAutoConfiguration(
        exclude = {DataSourceAutoConfiguration.class, JdbcRepositoriesAutoConfiguration.class}
)
@ConfigurationPropertiesScan
public class DbConfig extends AbstractJdbcConfiguration {


    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource auditDataSource() {
        return DataSourceBuilder.create().build();
    }


    @Override
    public JdbcCustomConversions jdbcCustomConversions() {
        return JdbcCustomConversionsUtil.jdbcCustomConversions();
    }

}