package org.example.sjpa.test;

import lombok.extern.slf4j.Slf4j;
import org.example.sjpa.SJpaApplication;
import org.example.sjpa.Stu;
import org.example.sjpa.StuController;
import org.example.sjpa.StuRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;


import java.util.List;

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
