
# spring-data-jdbc-mutidb

## 介绍
spring-data-jdbc多数据源的实践。

## 项目框架
spring-boot
spring-data-jdbc
h2-db
mockito

## 多数据源配置

配置文件参考[application.yaml](src/main/resources/application.yaml).
分别使用`spring.datasource.userdb`和`spring.datasource.orderdb`配置2个库的前缀。在UserDbConfig中配置jdbc的basePackages，jdbcOperationsRef，transactionManagerRef等。
```java
@Conditional(DBConfigConditional.class)
@Configuration
@EnableJdbcRepositories( basePackages = "org.example.sdj.mutidb.dbuser",
        jdbcOperationsRef = "userdbJdbcOperations",
        transactionManagerRef = "userdbTransactionManager",
        dataAccessStrategyRef = "userdbDataAccessStrategy"
)
@EnableAutoConfiguration(
        exclude = {DataSourceAutoConfiguration.class, JdbcRepositoriesAutoConfiguration.class}
)
@ConfigurationPropertiesScan
public class UserDbConfig extends AbstractJdbcConfiguration {

}
```
DBConfigConditional通过扫描EnableDbConfig注解可以方便的配置开启或者关闭某个数据库配置。

由于初始化脚本不能分别配置到不同数据库，所以本例使用了2个初始化好的数据库进行验证。

一般的Repository继承PagingAndSortingRepository，并拥有其所有功能，其默认的实现为SimpleJdbcRepository.
如果需要定制功能，则写一个RepositoryCustom接口和其实现类，在实现类中使用`@Qualifier`指定注入需要使用的jdbcOperations.
然后继承RepositoryCustom接口的类也继承了其功能，比较喜欢这种使用接口编程风格。
```java
    @Qualifier("orderdbJdbcOperations")
    @Autowired
    NamedParameterJdbcOperations jdbcOperations;
```

