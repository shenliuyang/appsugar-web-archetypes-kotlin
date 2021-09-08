package org.appsugar.archetypes.repository;

import lombok.extern.slf4j.Slf4j;
import org.appsugar.archetypes.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.repository
 * @className IntegrationDbImportTest
 * @date 2021-04-06  10:51
 */

@Slf4j
public class IntegrationDbImportTest extends BaseIntegrationTest {
    @Autowired
    private UserRepository repository;

    @Test
    public void testImportDb() {
        log.debug("testImportDb {}", repository.findAll());
    }

}
