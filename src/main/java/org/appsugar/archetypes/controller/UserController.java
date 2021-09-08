package org.appsugar.archetypes.controller;

import org.appsugar.archetypes.domain.User;
import org.appsugar.archetypes.domain.condition.UserCondition;
import org.appsugar.archetypes.repository.UserRepository;
import org.appsugar.archetypes.security.BitSecure;
import org.appsugar.archetypes.security.TwiceSecure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class UserController implements UserFacade {

    @Autowired
    UserRepository userRepository;

    @BitSecure("user:list")
    @Override
    public Page<User> list(@PathVariable int page, @PathVariable int size, @RequestBody UserCondition userCondition) {
        Page<User> result = userRepository.findAll(userRepository.toPredicate(userCondition), PageRequest.of(page, size));
        result.getContent().stream().forEach(e -> e.setRoles(null));
        return result;
    }

    @TwiceSecure("user:list")
    @Override
    public List<User> prettyList() {
        return null;
    }

    @TwiceSecure("user:delete")
    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }


}
