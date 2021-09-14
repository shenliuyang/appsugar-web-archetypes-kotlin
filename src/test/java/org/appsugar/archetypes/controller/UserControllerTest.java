package org.appsugar.archetypes.controller;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraph;
import com.querydsl.core.types.OrderSpecifier;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.appsugar.archetypes.domain.User;
import org.appsugar.archetypes.repository.UserRepository;
import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
@Slf4j
public class UserControllerTest extends BaseControllerTest {
    @MockBean
    private UserRepository userRepository;


    @Test
    @SneakyThrows
    @WithMockUser(authorities = "user:list")
    public void testList() {
        List<User> expectedUsers = Arrays.asList(new User(1l, "admin", "admin", "admin", "123456", null));

        Mockito.when(userRepository.findAll(Mockito.any(EntityGraph.class), Mockito.any(OrderSpecifier[].class))).thenReturn(expectedUsers);

        MvcResult mvcResult = mockMvc.perform(get(UserController.LIST_URL).contentType(CONTENT_TYPE_JSON)).andExpect(status().isOk()).andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        String expectedResult = objectMapper.writeValueAsString(expectedUsers);
        log.debug("testList expectedUsers is {}  real users is {}", expectedResult, result);
        // Assertions.assertEquals(expectedResult, result);
    }
}
