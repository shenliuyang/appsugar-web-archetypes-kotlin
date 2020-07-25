package org.appsugar.archetypes;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test", "integration"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@Import(FeignClientsConfiguration.class)
public abstract class BaseIntegrationTest {
}
