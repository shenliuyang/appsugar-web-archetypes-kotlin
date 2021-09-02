package org.appsugar.archetypes.security;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.appsugar.archetypes.domain.User;
import org.appsugar.archetypes.repository.UserRepository;
import org.appsugar.archetypes.system.Permissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * spring security 用户信息查找
 *
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.service
 * @className UserDetailServiceImpl
 * @date 2021-07-06  19:31
 */
@Service
@Slf4j
@ConfigurationProperties("management.security")
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    @Getter
    @Setter
    String username;
    @Getter
    @Setter
    String password;

    UserDetails randomUserDetails;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //排除内部系统访问 spring boot admin server
        if (StringUtils.equals(username, getUsername())) {
            return randomUserDetails;
        }
        User user = userRepository.findOneByLoginName(username);
        if (Objects.isNull(user)) {
            log.info("user  login failed because  {} do not exist", username);
            throw new UsernameNotFoundException(username);
        }
        String loginName = username;
        String pwd = encoder.encode(user.getPassword());
        String split = User.PERMISSION_SPLIT_CHAR;
        Set<String> permissions = user.getRoles().stream().flatMap(e -> Arrays.stream(e.getPermissions().split(","))).collect(Collectors.toSet());
        for (String s : user.getPermissions().split(split)) {
            permissions.add(s);
        }
        return new LoginUser(user.getId(), loginName, pwd, permissions, Permissions.modifyCount, System.currentTimeMillis());
    }

    @PostConstruct
    public void postConstruct() {
        this.randomUserDetails = new org.springframework.security.core.userdetails.User(username, encoder.encode(password), new HashSet<>());
    }
}
