package org.appsugar.archetypes.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.appsugar.archetypes.domain.dto.Response;
import org.appsugar.archetypes.system.advice.SystemControllerAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

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
@Slf4j
public class SecurityControllerAdvice {
    @Autowired
    private ObjectMapper objectMapper;


    /**
     * 处理spring security权限不足异常
     */
    @SneakyThrows
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public Response processAccessDenied(HttpServletRequest req, HttpServletResponse res) {
        SystemControllerAdvice.setErrorResponseHeader(res);
        return Response.UN_AUTHORIZATION;
    }

    /**
     * 处理需要二次登录授权
     */
    @SneakyThrows
    @ExceptionHandler(AuthorizationExceptions.ReAuthorizationException.class)
    @ResponseBody
    public Response processReAuthorizationException(HttpServletRequest req, HttpServletResponse res) {
        SystemControllerAdvice.setErrorResponseHeader(res);
        return Response.RE_AUTHORIZATION;
    }

    /**
     * 客户端权限信息已过期,需要重新登录
     */
    @SneakyThrows
    @ExceptionHandler(AuthorizationExceptions.AuthorizationExpiredException.class)
    @ResponseBody
    public Response processReAuthenticationException(HttpServletRequest req, HttpServletResponse res) {
        SystemControllerAdvice.setErrorResponseHeader(res);
        return Response.RE_AUTHENTICATION;
    }
}
