package org.appsugar.archetypes.repository;

import lombok.extern.slf4j.Slf4j;
import org.appsugar.archetypes.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 用户数据资源测试类
 */
@Slf4j
public class UserRepositoryTest extends BaseRepositoryTest {
    @Autowired
    UserRepository userRepository;


    @Test
    public void testFindAll() {
        List<User> users = userRepository.findAll();
        log.debug("testFindAll  result is {}", users);
    }

    @Test
    public void testFindByName() {
        String name = "newyoung";
        List<User> result = userRepository.findByName(name);
        log.debug("testFindByName name {} result is {}", name, result);
        Assertions.assertTrue(!result.isEmpty());
    }

}
