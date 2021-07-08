package org.appsugar.archetypes.controller;

import org.appsugar.archetypes.domain.User;
import org.appsugar.archetypes.domain.UserEntityGraph;
import org.appsugar.archetypes.repository.UserRepository;
import org.appsugar.archetypes.security.BitSecure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class UserController implements UserFacade {

    @Autowired
    UserRepository userRepository;

    @BitSecure("user:list")
    @Override
    public List<User> list() {
        return (List<User>) userRepository.findAll(UserEntityGraph.____().role().____.____());
    }
}
