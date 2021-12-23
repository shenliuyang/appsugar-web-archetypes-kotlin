package org.appsugar.archetypes.repository.jpa;

import lombok.val;
import org.appsugar.archetypes.repository.UserRepositoryCustom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.repository.jpa
 * @className UserRepositoryCustomTest
 * @date 2021-12-09  14:30
 */
@Import(UserRepositoryCustom.class)
public class UserRepositoryCustomTest extends  BaseJpaRepositoryTest {
    @Autowired
    private UserRepositoryCustom repositoryCustom;

    @Test
    public void testFindUserStat(){
        val result = repositoryCustom.findUserStat();
        logger.info("testFindUserStat {}",result);
    }
}
