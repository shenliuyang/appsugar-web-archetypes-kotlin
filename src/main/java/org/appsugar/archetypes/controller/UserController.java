package org.appsugar.archetypes.controller;

import lombok.Data;
import org.appsugar.archetypes.domain.User;
import org.appsugar.archetypes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Data
public class UserController {
    public static final String BASE_URL = "/user";
    public static final String LIST_URL = BASE_URL + "/list";
    @Autowired
    UserRepository userRepository;

    @GetMapping(UserController.LIST_URL)
    public List<User> list() {
        return getUserRepository().findAll();
    }
}
