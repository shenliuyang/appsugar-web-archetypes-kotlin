package org.appsugar.archetypes.controller;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraphUtils;
import org.appsugar.archetypes.domain.User;
import org.appsugar.archetypes.repository.UserRepository;
import org.appsugar.archetypes.security.BitSecure;
import org.appsugar.archetypes.security.TwiceSecure;
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
        return (List<User>) userRepository.findAll(EntityGraphUtils.fromAttributePaths(User.Fields.roles));
    }

    @TwiceSecure("user:list")
    @Override
    public List<User> prettyList() {
        return list();
    }

    @TwiceSecure("user:delete")
    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }


}
