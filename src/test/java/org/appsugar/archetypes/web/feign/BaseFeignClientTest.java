package org.appsugar.archetypes.web.feign;

import feign.Contract;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.appsugar.archetypes.BaseIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;

public abstract class BaseFeignClientTest extends BaseIntegrationTest {
    @LocalServerPort
    protected int port;
    @Autowired
    protected Decoder decoder;
    @Autowired
    protected Encoder encoder;

    @Autowired
    protected Contract contract;

    protected <T> T createClient(Class<T> clazz) {
        return Feign.builder()
                .encoder(encoder)
                .decoder(decoder)
                .contract(contract)
                .target(clazz, "http://localhost:" + port);
    }
}
