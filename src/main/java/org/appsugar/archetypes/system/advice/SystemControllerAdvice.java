package org.appsugar.archetypes.system.advice;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.appsugar.archetypes.domain.dto.Response;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    /**
     * 参数验证异常直接通知客户端
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Response<Void> argumentValidHandler(MethodArgumentNotValidException ex) {
        BindingResult br = ex.getBindingResult();
        StringBuilder sb = new StringBuilder();
        for (FieldError error : br.getFieldErrors()) {
            sb.append("[field=").append(error.getField()).append(",value=").append(error.getRejectedValue());
            sb.append(",msg=").append(error.getDefaultMessage()).append("]");
        }
        return Response.error(sb.toString());
    }

    /**
     * 系统级别异常打日志
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Response<Void> exceptionHandler(HttpServletRequest req, Exception exception) {
        log.error("process request cause exception uri {} ", req.getRequestURI(), exception);
        return Response.error(ExceptionUtils.getRootCauseMessage(exception));
    }
}

