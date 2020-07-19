package org.appsugar.archetypes.web.feign;


import lombok.extern.slf4j.Slf4j;
import org.appsugar.archetypes.controller.UserFacade;
import org.appsugar.archetypes.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
public class UserFacadeTest extends BaseFeignClientTest {
    private UserFacade userFacade;

    @Test
    public void testList() {
        List<User> users = userFacade.list();
        log.debug("testList users is {}", users);
        Assertions.assertFalse(users.isEmpty(), "cannot find any user by feign client");
    }

    @PostConstruct
    public void post() {
        this.userFacade = createClient(UserFacade.class);
    }
}
