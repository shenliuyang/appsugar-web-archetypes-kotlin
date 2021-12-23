package org.appsugar.archetypes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.appsugar.archetypes.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest
public abstract class BaseControllerTest extends BaseTest {
    protected static final String CONTENT_TYPE_JSON = "application/json";
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
}
