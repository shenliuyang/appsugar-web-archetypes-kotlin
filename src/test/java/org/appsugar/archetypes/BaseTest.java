package org.appsugar.archetypes;

import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.Charset;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public abstract class BaseTest {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        byte[] c = {-30, -128, -114, 51, 48, 57, 67, 50, 51, 50, 69, 48, 66, 57, 66};
        System.out.println(new String(c, Charset.forName("utf-8")));
    }
}
