package org.appsugar.archetypes.repository.kjdbc;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
public class UserJdbcEntityRepositoryTest extends BaseJdbcRepositoryTest {
    @Autowired
    UserJdbcEntityRepository userJdbcEntityRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    @SneakyThrows
    public void testFindByName() {
        val name = "newyoung";
        val result = userJdbcEntityRepository.findByName(name);
        log.debug("testFindByName name is {} result is {}", name, result);
        Assertions.assertNotNull(result, "cannot find user by name : " + name);
    }
}
