package org.appsugar.archetypes.web.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;

@RestClientTest(GoogleRestClient.class)
public class GoogleRestClientTest extends BaseRestTest {

    @Autowired
    private GoogleRestClient googleRestClient;

    @Test
    public void testIndex() {
        String response = "hello,test rest client for google";
        this.server.expect(MockRestRequestMatchers.requestTo(GoogleRestClient.BASE_URL))
                .andRespond(MockRestResponseCreators.withSuccess(response, MediaType.TEXT_PLAIN));
        String result = googleRestClient.index();
        Assertions.assertEquals(response, result);
    }

}
