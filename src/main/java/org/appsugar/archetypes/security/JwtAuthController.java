package org.appsugar.archetypes.security;

import lombok.extern.slf4j.Slf4j;
import org.appsugar.archetypes.domain.User;
import org.appsugar.archetypes.domain.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @PostMapping("login")
    public ResponseEntity<Response<Void>> login(@RequestBody User request) {
        log.debug("login user is{}", request);
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getLoginName(), request.getPassword()));
            LoginUser user = (LoginUser) authenticate.getPrincipal();

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, JwtTokenFilter.startWith + jwtTokenUtil.generateAccessToken(user))
                    .body(new Response<>());
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
