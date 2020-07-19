package org.appsugar.archetypes.web.rest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;

@RestClientTest(BaiduRestClient.class)
public class BaiduRestClientTest extends BaseRestTest {

    @Autowired
    private BaiduRestClient baiduRestClient;

    @Test
    public void testIndex() {
        String response = "hello,test rest client for baidu";
        this.server.expect(MockRestRequestMatchers.requestTo(BaiduRestClient.BASE_URL))
                .andRespond(MockRestResponseCreators.withSuccess(response, MediaType.TEXT_PLAIN));
        String result = baiduRestClient.index();
        Assertions.assertEquals(response, result);
    }

}
