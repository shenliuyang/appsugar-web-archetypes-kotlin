package org.appsugar.archetypes.repository.jpa;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.appsugar.archetypes.domain.User;
import org.appsugar.archetypes.domain.UserCondition;
import org.appsugar.archetypes.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 用户数据资源测试类
 */
@Slf4j
public class UserRepositoryTest extends BaseJpaRepositoryTest {
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

    @Test
    public void testFindByCondition() {
        String name = "new";
        UserCondition condition = new UserCondition();
        condition.setName(name);
        Iterable<User> users = userRepository.findAll(userRepository.toPredicate(condition));
        log.debug("testFindByCondition  name is {}  result is {}", name, Lists.newLinkedList(users));
    }
}
