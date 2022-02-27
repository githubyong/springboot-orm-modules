
# spring-data-jpa-h2-min

## 介绍
spring-data-jpa集成h2 db 测试

## 项目框架
spring-boot
spring-data-jpa
h2-db
mockito

## h2-db

### 简介:
H2数据库是一个开源的关系型数据库。
H2是一个采用java语言编写的嵌入式数据库引擎，只是一个类库（即只有一个 jar 文件），可以直接嵌入到应用项目中，不受平台的限制

### 应用场景：

+ 可以同应用程序打包在一起发布，可以非常方便地存储少量结构化数据
+ 可用于单元测试
+ 可以用作缓存，即当做内存数据库

### spring-boot 使用h2:

配置文件参考[application.yaml](src/main/resources/application.yaml),启动Application后，会使用配置文件中配置的schema和data，在指定的位置  
并插入数据。启动完成后输入配置的web地址+${spring.h2.console.path} 进行访问，比如：http://localhost:9002/h2-console  
如图:

![](img/img1.png "h2 web console")



说明:
+ jdbc-url填写application.yaml中配置的地址.示例表示使用文件持久化，~表示系统当前用户的目录，启动成功后会生成文件，如本例会生成 C:\Users\yong\sjpa-db-min1.mv.db文件
+ username和password也可以在application.yaml中进行配置，本例没有配置，默认为空，直接点击connect即可连接。

登录成功后图下图，可以执行查询

![](img/img2.png)

### h2优点:
无需安装真实的数据库，通过简单的配置就能实现真实数据库的功能。通过配置程序启动的动作，不会污染测试环境，非常适合做一些框架的探索。
比如本文就简单的实现了spring-data-jpa的测试。

##  mockito 使用
### mockito 简介
Mockito 是一个强大的用于 Java 开发的模拟测试框架, 通过 Mockito 我们可以创建和配置 Mock 对象, 进而简化有外部依赖的类的测试.

### mockito demo
本例将使用一个简单的例子测试mockito的spy监控真实对象
```java

@SpringBootTest(classes = SJpaApplication.class)
@ExtendWith(SpringExtension.class) //导入Spring测试框架
@AutoConfigureMockMvc
@Slf4j
public class StuControllerMockTest {

    @SpyBean
    @Autowired
    StuRepository stuRepository;

    @SpyBean
    @Autowired
    StuController stuController;

    private MockMvc mockMvc;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);//这句话执行以后，service自动注入到controller中。
        // 构建mockMvc环境
        mockMvc = MockMvcBuilders.standaloneSetup((stuController)).build();
    }

    @Test
    @DisplayName("mockResult 测试StuController")
    void testFindAllUseHttp() throws Exception {
        MvcResult mockResult = mockMvc.perform(MockMvcRequestBuilders.get("/stus/list"))
                .andReturn();
        Assertions.assertEquals(mockResult.getResponse().getStatus(), HttpStatus.OK.value());
        verify(stuController, times(1)).findAll();//监控stuController至少被调用1次
        log.info(mockResult.getResponse().getContentAsString());
    }

    @Test
    void testFindAllUseController() {
        List<Stu> list = stuController.findAll();
        verify(stuRepository, times(1)).findAll();//监控stuRepository至少被调用1次
        Assertions.assertEquals(list.size(), 5);
    }

}


```

更多功能参考[Mockito 中文文档](https://github.com/hehonghui/mockito-doc-zh#21) .