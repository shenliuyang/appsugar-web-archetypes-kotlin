package org.appsugar.archetypes;

import org.appsugar.archetypes.repository.DbImportConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test", "integration"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@Import(DbImportConfiguration.class)
public abstract class BaseIntegrationTest {
    protected Logger logger = LoggerFactory.getLogger(BaseIntegrationTest.class);
}
