package org.appsugar.archetypes.repository.jpa;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.appsugar.archetypes.domain.User;
import org.appsugar.archetypes.domain.condition.UserCondition;
import org.appsugar.archetypes.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
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
        val result = userRepository.findAll(userRepository.toPredicate(c), userRepository.fromPaths(User.Fields.roles));
        logger.debug("testFindAndDynamicFetch user is {}", result);
    }

    @SneakyThrows
    @Test
    public void testFindAndWithoutFetch() {
        UserCondition c = new UserCondition();
        val result = userRepository.findAll(userRepository.toPredicate(c));
        logger.debug("testFindAndDynamicFetch user is {}", result);
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

    @RepeatedTest(2)
    public void testFindByCondition() {
        String name = "new";
        UserCondition condition = new UserCondition();
        condition.setName(name);
        Iterable<User> users = userRepository.findAll(userRepository.toPredicate(condition));
        Assertions.assertTrue(users != null);
        // log.debug("testFindByCondition  name is {}  result is {}", name, Lists.newLinkedList(users));
    }

    @Test
    public void testSave(){
        User u = new User();
        //u.setId(-1l);
        u.setName("test");
        u.setLoginName("test");
        u.setPassword("test");
        userRepository.save(u);
    }
}
