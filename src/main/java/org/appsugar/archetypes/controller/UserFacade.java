package org.appsugar.archetypes.controller;

import org.appsugar.archetypes.domain.User;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 用户访问接口
 *
 * @author shenl
 */
public interface UserFacade {
    String BASE_URL = "/user";
    String LIST_URL = BASE_URL + "/list";

    /**
     * 查询所有用户
     */
    @GetMapping(UserController.LIST_URL)
    List<User> list();
}
