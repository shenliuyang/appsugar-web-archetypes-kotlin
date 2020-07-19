package org.appsugar.archetypes.controller;

import lombok.Data;
import org.appsugar.archetypes.domain.User;
import org.appsugar.archetypes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Data
public class UserController implements UserFacade {
    
    @Autowired
    UserRepository userRepository;


    public List<User> list() {
        return getUserRepository().findAll();
    }
}
