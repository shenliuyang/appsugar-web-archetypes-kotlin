package org.appsugar.archetypes.system.advice;

import lombok.extern.slf4j.Slf4j;
import org.appsugar.archetypes.domain.dto.Response;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.system.advice
 * @className ControllerAdvice
 * @date 2021-07-11  21:53
 */
@ControllerAdvice
@Slf4j
public class SystemControllerAdvice {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Response<Void> exceptionHandler(HttpServletRequest req, Exception exception) {
        log.error("process request cause exception ", exception);
        return Response.error(exception.getMessage());
    }
}
