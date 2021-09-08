package org.appsugar.archetypes.security.jwt;

import lombok.extern.slf4j.Slf4j;
import org.appsugar.archetypes.domain.User;
import org.appsugar.archetypes.security.LoginUser;
import org.appsugar.archetypes.service.UserService;
import org.appsugar.archetypes.system.Permissions;
import org.appsugar.archetypes.system.advice.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.security
 * @className JwtAuthController
 * @date 2021-07-07  17:00
 */
@RestController
@RequestMapping("api/public")
@Slf4j
public class JwtAuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserService userService;

    static BusinessException usernameOrPasswordError = new BusinessException("用户账号或密码错误");

    @PostMapping("login")
    public ResponseEntity<User> login(@Valid @RequestBody RequestUser request) {
        log.debug("login user is {}", request);
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            LoginUser user = (LoginUser) authenticate.getPrincipal();
            JwtUser jwtUser = fromLoginUser(user);
            User u = new User();
            u.setPermissions(Permissions.encodeToStringPermission(user.getPermissions()));
            u.setName(user.getUsername());
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenFilter.startWith + jwtTokenUtil.generateAccessToken(jwtUser))
                    .body(u);
        } catch (BadCredentialsException ex) {
            throw usernameOrPasswordError;
        }
    }

    private JwtUser fromLoginUser(LoginUser loginUser) {
        String token = userService.generateUserToken(loginUser);
        return JwtUser.fromLoginUser(loginUser, token);
    }
}
