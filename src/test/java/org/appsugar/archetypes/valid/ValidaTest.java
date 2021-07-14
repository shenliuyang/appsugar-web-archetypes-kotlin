package org.appsugar.archetypes.valid;

import lombok.val;
import org.appsugar.archetypes.BaseIntegrationTest;
import org.appsugar.archetypes.security.jwt.RequestUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Validator;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.valid
 * @className ValidaTest
 * @date 2021-07-13  10:41
 */
public class ValidaTest extends BaseIntegrationTest {
    @Autowired
    private Validator v;

    @Test
    public void testValidate() {
        RequestUser u = new RequestUser();
        val err = v.validate(u);
        logger.debug("error is {}", err);
    }

}
