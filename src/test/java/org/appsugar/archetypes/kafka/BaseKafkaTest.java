package org.appsugar.archetypes.kafka;

import org.appsugar.archetypes.BaseIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;


public abstract class BaseKafkaTest extends BaseIntegrationTest {
    @Autowired
    protected KafkaTemplate<String, String> template;

    protected void preDestory() {
        
    }

}
