package org.appsugar.archetypes.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.appsugar.archetypes.domain.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.security
 * @className SecurityControllerAdvice
 * @date 2021-07-11  21:21
 */
@ControllerAdvice
public class SecurityControllerAdvice {
    @Autowired
    private ObjectMapper objectMapper;

    private byte[] unAuthorizationMsg;
    private byte[] reAuthorizationMsg;
    private byte[] reAuthenticationMsg;


    /**
     * 处理spring security权限不足异常
     */
    @SneakyThrows
    @ExceptionHandler(AccessDeniedException.class)
    public void processAccessDenied(HttpServletRequest req, HttpServletResponse res) {
        res.getOutputStream().write(unAuthorizationMsg);
    }

    /**
     * 处理需要二次登录授权
     */
    @SneakyThrows
    @ExceptionHandler(AuthorizationExceptions.ReAuthorizationException.class)
    public void processReAuthorizationException(HttpServletRequest req, HttpServletResponse res) {
        res.getOutputStream().write(reAuthorizationMsg);
    }

    /**
     * 客户端权限信息已过期,需要重新登录
     */
    @SneakyThrows
    @ExceptionHandler(AuthorizationExceptions.AuthorizationExpiredException.class)
    public void processReAuthenticationException(HttpServletRequest req, HttpServletResponse res) {
        res.getOutputStream().write(reAuthenticationMsg);
    }


    @PostConstruct
    @SneakyThrows
    public void postConstruct() {
        unAuthorizationMsg = objectMapper.writeValueAsBytes(Response.UN_AUTHORIZATION);
        reAuthorizationMsg = objectMapper.writeValueAsBytes(Response.RE_AUTHORIZATION);
        reAuthenticationMsg = objectMapper.writeValueAsBytes(Response.RE_AUTHENTICATION);
    }
}
