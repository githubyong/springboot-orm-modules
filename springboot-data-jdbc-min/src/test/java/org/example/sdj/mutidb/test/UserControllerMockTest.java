package org.example.sdj.mutidb.test;

import lombok.extern.slf4j.Slf4j;
import org.example.sdj.min.SdjMinApplication;
import org.example.sdj.min.UserController;
import org.example.sdj.min.dao.UserInfoRepository;
import org.example.sdj.min.entity.UserInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
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

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = SdjMinApplication.class)
@ExtendWith(SpringExtension.class) //导入Spring测试框架
@AutoConfigureMockMvc
@Slf4j
public class UserControllerMockTest {

    @SpyBean
    @Autowired
    UserInfoRepository userInfoRepository;

    @SpyBean
    @Autowired
    UserController userController;

    private MockMvc mockMvc;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);//这句话执行以后，service自动注入到controller中。
        // 构建mockMvc环境
        mockMvc = MockMvcBuilders.standaloneSetup((userController)).build();
    }

    @Test
    @DisplayName("mockResult 测试StuController")
    void testFindAllUseHttp() throws Exception {
        MvcResult mockResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/list"))
                .andReturn();
        Assertions.assertEquals(mockResult.getResponse().getStatus(), HttpStatus.OK.value());
        verify(userController, times(1)).findAll();//监控stuController至少被调用1次
        log.info(mockResult.getResponse().getContentAsString());
    }

    @Test
    void testFindAllUseController() {
        List<UserInfo> list = userController.findAll();
        verify(userInfoRepository, times(1)).findAll();//监控stuRepository至少被调用1次
        Assertions.assertEquals(list.size(), 10);
    }

}
