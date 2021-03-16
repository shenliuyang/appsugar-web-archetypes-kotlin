package org.appsugar.archetypes.repository.jpa;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.appsugar.archetypes.domain.User;
import org.appsugar.archetypes.domain.UserCondition;
import org.appsugar.archetypes.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据资源测试类
 */
@Slf4j
public class UserRepositoryTest extends BaseJpaRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @SneakyThrows
    @Test
    public void testBatchInsertAndUpdate() {
        val users = new ArrayList<User>();
        for (int i = 1; i <= 10; i++) {
            val user = new User();
            user.setName("z" + i);
            user.setAddress("xxx" + i);
            user.setAge(i);
            user.setEmail("xxx" + i);
            user.setLoginName("login" + i);
            users.add(user);
        }
        userRepository.saveAll(users);
        userRepository.flush();
        users.subList(0, 5).forEach(e -> e.setAge(18));
        users.subList(5, 10).forEach(e -> e.setAddress("xxx18"));
        userRepository.saveAll(users);
        userRepository.flush();
    }

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
        Assertions.assertFalse(result.isEmpty());
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
