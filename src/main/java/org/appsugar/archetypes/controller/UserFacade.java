package org.appsugar.archetypes.controller;

import org.appsugar.archetypes.domain.User;
import org.appsugar.archetypes.domain.condition.UserCondition;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 用户访问接口
 *
 * @author shenl
 */
public interface UserFacade {
    String BASE_URL = "/user";
    String LIST_URL = BASE_URL + "/list/{page}/{size}";
    String PRETTY_LIST_URL = BASE_URL + "/pretty_list";
    String DELETE_URL = BASE_URL + "/delete/{id}";

    /**
     * 查询所有用户
     */
    @RequestMapping(LIST_URL)
    Page<User> list(@PathVariable int page, @PathVariable int size, UserCondition userCondition);

    @GetMapping(PRETTY_LIST_URL)
    List<User> prettyList();

    @PostMapping(DELETE_URL)
    void delete(@PathVariable("id") Long id);
}
