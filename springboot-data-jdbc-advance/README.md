
# spring-data-jdbc-advance

## 介绍
spring-data-jdbc 高级功能实践。

## 项目框架
spring-boot
spring-data-jdbc
h2-db
mockito

## 定制通用JdbcRepository
通过@EnableJdbcRepositories配置定制的repositoryBaseClass和repositoryFactoryBeanClass.

```java
@Configuration
@EnableJdbcRepositories(basePackages = "org.example.sdj.advance",
        repositoryBaseClass = MyJdbcRepositoryImpl.class,
        repositoryFactoryBeanClass = MyJdbcRepositoryFactoryBean.class
)
@EnableAutoConfiguration(
        exclude = {DataSourceAutoConfiguration.class, JdbcRepositoriesAutoConfiguration.class}
)
@ConfigurationPropertiesScan
public class DbConfig extends AbstractJdbcConfiguration {
    //其它配置
}
```

###主要组件
+ MyJdbcRepositoryImpl 继承SimpleJdbcRepository，其内部依赖自定义组件BaseDao，可以调用BaseDao实现一些自定义通用功能，比如:findByEntity(T t)，updateSelective(T t)等
  
+ MyJdbcRepositoryFactoryBean 主要管理JdbcRepositoryFactory。 从spring-data-jdbc的JdbcRepositoryFactoryBean移植而来。
  由于JdbcRepositoryFactoryBean内的成员变量都是私有的。所以没有没有继承，而是通过copy代码的方式，重写了RepositoryFactorySupport doCreateRepositoryFactory()的方法，返回JdbcRepositoryFactory。
```java
public class MyJdbcRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>
        extends TransactionalRepositoryFactoryBeanSupport<T, S, ID> implements ApplicationEventPublisherAware {
    // other code copy from JdbcRepositoryFactoryBean
  
  @Override
  protected RepositoryFactorySupport doCreateRepositoryFactory() {

    MyJdbcRepositoryFactory jdbcRepositoryFactory = new MyJdbcRepositoryFactory(dataAccessStrategy, mappingContext,
            converter, dialect, publisher, operations);
    jdbcRepositoryFactory.setQueryMappingConfiguration(queryMappingConfiguration);
    jdbcRepositoryFactory.setEntityCallbacks(entityCallbacks);
    jdbcRepositoryFactory.setBeanFactory(beanFactory);

    return jdbcRepositoryFactory;
  }
}
```

+ MyJdbcRepositoryFactory 继承JdbcRepositoryFactory，该组件主要重写了getTargetRepository()方法，在其最后一步传入构造MyJdbcRepositoryImpl所需的参数
```java
public class MyJdbcRepositoryFactory extends JdbcRepositoryFactory {

  @Override
  protected Object getTargetRepository(RepositoryInformation repositoryInformation) {
    JdbcAggregateTemplate template = new JdbcAggregateTemplate(publisher, context, converter, accessStrategy);

    if (entityCallbacks != null) {
      template.setEntityCallbacks(entityCallbacks);
    }

    RelationalPersistentEntity<?> persistentEntity = context
            .getRequiredPersistentEntity(repositoryInformation.getDomainType());

    return instantiateClass(repositoryInformation.getRepositoryBaseClass(), template, persistentEntity, this.operations, this.dialect, this.converter);//构造MyJdbcRepositoryImpl所需的参数
  }
}

```

+ BaseDao 里面写了一些通用的方法，比如传入sql和分页参数进行查询(List<T> findAllBySQL(String sql, @Nullable Pageable pageable))，
  按实体进行查询( <T> List<T> findByEntity(@NonNull T t, @Nullable Pageable pageable))等。
  由于spring-data-jdbc的@Query注解不支持 limit :m :n 这种语法，所以需要自己拼接分页sql.
  BaseDao的设计思路是作为一个组件被Repotitory层调用，这样避免在其它层调用sql,而把sql分散到各个地方。
  
+ EntityUtil 主要是提供一些和Entity相关的方法，比如通过反射获取主键，通过反射给某个Entity设置属性等。该类参考了spring-data-jdbc BasicJdbcConverter.mapRow()方法。
  spring-data-jdbc的一些RowMapper都会调用此方法把ResultSet转换成java Entity.尤其有时候数据库的类型和java entity的属性类型可能需要converter进行转换。
  所以在自己写mapRow的时候也需要调用类似的逻辑来保证converter的一致性。

  spring-data-jdbc的Entity和注解并不是十分理想，比如在UserAddress的entity中，想加下userName,而这个属性来自user_info表，通过spring-data-jdbc的注解无法完成。
  所以本例定义了一个@CustomField注解，使用其中的refValue表示引用某个表的某列属性，而且需要加上@Transient注解，这样才能保证data-jdbc通用的方法不会因为这个属性而生成它不认识的属性。
  通过自定义的sql查出来所需的属性，再通过EntityUtil的相关方法把值设置到Entity中。这时又有新的问题，因为加了@Transient注解后，data-jdbc将不认为这个属性是Entiy的一个PersistentProperty,无法调用PropertyAccessor的方法。
  ReflectionUtils.doWithFields 和ReflectionUtils.doWithMethods可以帮到我们。在setPropertyValue时，如果发现了property在Entity中就调用PropertyAccessor的方法赋值。
  如果不在则结合上面的ReflectionUtils相关方法找到对应的反射Method,然后在invoke前调用convertIfNecessary()进行转换，做了同ConvertingPropertyAccessor一样的功能。
```java
public class EntityUtil {
    //...
  public static List<Field> getAllFields(Class domainType) {
    List<Field> list = new ArrayList<>();
    ReflectionUtils.doWithFields(domainType, field -> {
      list.add(field);
    });
    return list;
  }

  public static List<Method> getAllMethods(Class domainType) {
    List<Method> methods = new ArrayList<>();
    ReflectionUtils.doWithMethods(domainType, method -> {
      methods.add(method);
    });
    return methods;
  }
}
```  

+ ResultReadUtil 该组件主要用于读取ResultSet和调用EntityUtil把读到的结果转换成Entity.
  在读取结果时，存放于ResultRowLine，其内部有个Table<String, String, Object> rowLine，利用guava的table存储每一行数据，实际上就是 table,column,value这样的数据，从ResultSetMetaData可以获取这样的属性。
  在readEntity转换成Entity时先读取Entity上的注解的表名，从上述的table读取到其valueMap,由于我们还有自定义的@CustomField注解有refValue,所以也会收集这些属性，从table中读取其他表的引用属性。
  属性都读取到map中后，通过反射获得entity的属性，从valueMap中读取值，通过EntityUtil进行赋值。再用extendRow(ResultRowLine rowLine, Object currentLevelEntity,..)把连接查询的值赋值到Entity的其它Entity属性。
  由主Entity的Id来区分是不是同一个Entity，这样最终实现自动解析连接查询。
  
+ CustomField 该组件是自定义注解，用于扩展@Column。除上述提到的refValue外，还有qeuryOperator用于在使用findByEntity时过滤条件是用like还是equal;
  qeuryEmpty用于在findByEntity时不过滤是否为无效数据，比如有时需要查询status=0的情况;updateEmpty用于updateEntity时不判断属性是否无效;readOnlyField用于updateEmpty时不拼接此属性的sql.
  
+ BaseEntityResultSetExtractor 用于自动解析连接查询转换成Entity。

+ ResultMapperFactory 该类implements ApplicationContextAware，用于在程序初始化的时候为每个Entity或者DTO生成BaseEntityResultSetExtractor，在Repository的注解@Query中，使用resultSetExtractorRef调用

注: 由于@CustomField的refValue或者类似的使用自有的ResultSetExtractor解析的Entity,其中的属性需要特定的sql才能查出，所以这种扩展的Entity还是命名DTO为好，
  以此和数据库实体的Entity分开。数据库实体Entity和Table一一对应，ETO实体表示扩展，需要特定sql和Extractor才能有特定的属性。


## spring-data-jdbc 不足
+ one-to-many many-to-one等支持不足 如下:
    - 当使用`@MappedCollection(idColumn = "ORDER_SN", keyColumn = "ID")`时,sql=` SELECT "order_item"."*" FROM "order_item" WHERE "order_item"."ORDER_SN" = ? ORDER BY "ORDER_SN"` 参数是OrderInfo的id,最终执行会报错，因为参数类型不一致
    - 当使用`@MappedCollection(idColumn = "ID",keyColumn ="ORDER_SN")`时,sql=` SELECT "order_item"."*" FROM "order_item" WHERE "order_item"."ID" = ? ORDER BY "ORDER_SN"] order_item.ID` 参数是OrderInfo的id，执行正常，但查不到数据
+ @ReadOnlyProperty 不是那么理想，使用该注解的property在spring-data-jdbc的一些默认方法时会把这个属性也带到sql语句中，无法达成手动读取这个property的功能。
  
```java
package org.example.sdj.advance.invalid;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("order_info")
public class OrderInfoWithItems extends BaseEntity implements Serializable {
         //	@MappedCollection
        @MappedCollection(idColumn = "ORDER_SN", keyColumn = "ID")    //  [SELECT "order_item"."*" FROM "order_item" WHERE "order_item"."ORDER_SN" = ? ORDER BY "ORDER_SN"]
        //	@MappedCollection(idColumn = "ID",keyColumn ="ORDER_SN" )//  [SELECT "order_item"."*" FROM "order_item" WHERE "order_item"."ID" = ? ORDER BY "ORDER_SN"] order_item.ID
     	List<OrderItem> orderItems;
}
  
```