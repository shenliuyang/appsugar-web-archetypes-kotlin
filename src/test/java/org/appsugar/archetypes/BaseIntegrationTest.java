package org.appsugar.archetypes;

import org.appsugar.archetypes.repository.BaseJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test", "integration"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@Import(FeignClientsConfiguration.class)
//fix dataJpa and dataJdbc repository registry conflict
@EnableJpaRepositories(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = BaseJpaRepository.class))
@EmbeddedKafka(
        bootstrapServersProperty = "spring.kafka.bootstrap-servers")
public abstract class BaseIntegrationTest {
    protected Logger logger = LoggerFactory.getLogger(BaseIntegrationTest.class);
}
