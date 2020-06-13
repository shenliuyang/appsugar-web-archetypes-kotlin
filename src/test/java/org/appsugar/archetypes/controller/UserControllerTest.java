package org.appsugar.archetypes.controller;

import lombok.extern.slf4j.Slf4j;
import org.appsugar.archetypes.domain.User;
import org.appsugar.archetypes.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

/**
 * 用户控制器测试
 * 使用mock进行单元测试
 */
@Slf4j
public class UserControllerTest extends BaseControllerTest {
    @MockBean
    private UserRepository userRepository;

    @Test
    public void testList() throws Throwable {
        List<User> expectedUsers = Arrays.asList(new User(0l, "admin", "admin", "beijing", "admin@pronhub.com", 1));
        Mockito.when(userRepository.findAll())
                .thenReturn(expectedUsers);
        List<User> resultUsers = webClient.get().uri(UserController.LIST_URL).exchange().expectBodyList(User.class).returnResult().getResponseBody();
        log.debug("testList expectedUsers is {}  real users is {}", expectedUsers, resultUsers);
        Assertions.assertEquals(expectedUsers, resultUsers);
    }

}