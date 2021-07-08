package org.appsugar.archetypes.repository;

import org.appsugar.archetypes.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.repository
 * @className IntegrationDbImportTest
 * @date 2021-04-06  10:51
 */
@Import(DbImportConfiguration.class)
public class IntegrationDbImportTest extends BaseIntegrationTest {


    @Test
    public void testImportDb() {

    }

}
