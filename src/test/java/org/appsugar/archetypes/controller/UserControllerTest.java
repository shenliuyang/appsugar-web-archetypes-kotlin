package org.appsugar.archetypes.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.appsugar.archetypes.domain.User;
import org.appsugar.archetypes.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 用户控制器测试
 * 使用mock进行单元测试
 */
@Slf4j
public class UserControllerTest extends BaseControllerTest {
    @MockBean
    private UserRepository userRepository;

    @Test
    @SneakyThrows
    public void testList() {
        List<User> expectedUsers = Arrays.asList(new User(0l, "admin", "admin", "beijing", "admin@pronhub.com", 1));
        Mockito.when(userRepository.findAll())
                .thenReturn(expectedUsers);
        MvcResult mvcResult = mockMvc.perform(get(UserController.LIST_URL).contentType(CONTENT_TYPE_JSON)).andExpect(status().isOk()).andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        String expectedResult = objectMapper.writeValueAsString(expectedUsers);
        log.debug("testList expectedUsers is {}  real users is {}", expectedResult, result);
        Assertions.assertEquals(expectedResult, result);
    }
}
