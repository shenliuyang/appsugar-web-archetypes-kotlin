package org.appsugar.archetypes.controller;

import org.appsugar.archetypes.BaseTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;


@ExtendWith(SpringExtension.class)
@WebFluxTest
public abstract class BaseControllerTest extends BaseTest {

    @Autowired
    protected WebTestClient webClient;
}
