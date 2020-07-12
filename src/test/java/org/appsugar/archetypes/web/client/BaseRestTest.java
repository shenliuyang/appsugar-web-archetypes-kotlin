package org.appsugar.archetypes.web.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.appsugar.archetypes.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.client.MockRestServiceServer;


public abstract class BaseRestTest extends BaseTest {
    @Autowired
    protected MockRestServiceServer server;

    @Autowired
    protected ObjectMapper objectMapper;
}
