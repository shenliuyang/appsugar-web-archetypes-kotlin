package org.appsugar.archetypes.web.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GoogleRestClient {
    public static final String BASE_URL = "https://www.google.com";
    public static final String INDEX_URL = BASE_URL;

    private RestTemplate restTemplate;

    public GoogleRestClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    /**
     * 获取首页信息
     */
    public String index() {
        return restTemplate.getForObject(INDEX_URL, String.class);
    }
}
