package org.appsugar.archetypes.repository.jpa;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.appsugar.archetypes.domain.User;
import org.appsugar.archetypes.domain.UserCondition;
import org.appsugar.archetypes.domain.UserEntityGraph;
import org.appsugar.archetypes.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据资源测试类
 */
@Slf4j
public class UserRepositoryTest extends BaseJpaRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager em;

    
    @SneakyThrows
    @Test
    public void testFindOneByName() {
        val name = "newyoung";
        var user = userRepository.findOneByName(name);
        user.setName("123");
        userRepository.flush();
        em.clear();
        user.setLoginName("xxx1");
        user = userRepository.save(user);
        userRepository.flush();
        logger.debug("testFindOneByName classname is {} user is {}", user.getClass(), user);

    }

    @SneakyThrows
    @Test
    public void testFindAndDynamicFetch() {
        UserCondition c = new UserCondition();
        val result = userRepository.findAll(userRepository.toPredicate(c), UserEntityGraph.____().role().____.____());
        logger.debug("testFindAndDynamicFetch user is {}", result);
    }

    @SneakyThrows
    @Test
    public void testFindAndWithoutFetch() {
        UserCondition c = new UserCondition();
        val result = userRepository.findAll(userRepository.toPredicate(c));
        logger.debug("testFindAndDynamicFetch user is {}", result);
    }

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

        val u1 = new User();
        u1.setId(users.get(0).getId());
        u1.setEmail("xxx");
        userRepository.save(u1);
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
