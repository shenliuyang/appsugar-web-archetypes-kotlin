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
import javax.servlet.http.HttpServletResponse;

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
    private static final String ERROR_KEY = "error";
    private static final String ERROR_VALUE = "1";

    public final static void setErrorResponseHeader(HttpServletResponse res) {
        res.addHeader(ERROR_KEY, ERROR_VALUE);
    }

    /**
     * 参数验证异常直接通知客户端
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Response argumentValidHandler(MethodArgumentNotValidException ex, HttpServletResponse res) {
        SystemControllerAdvice.setErrorResponseHeader(res);
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
    public Response exceptionHandler(HttpServletRequest req, Exception exception, HttpServletResponse res) {
        SystemControllerAdvice.setErrorResponseHeader(res);
        log.error("process request cause exception uri {} ", req.getRequestURI(), exception);
        return Response.error(ExceptionUtils.getRootCauseMessage(exception));
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public Response busExceptionHandler(HttpServletRequest req, BusinessException exception, HttpServletResponse res) {
        SystemControllerAdvice.setErrorResponseHeader(res);
        return Response.error(ExceptionUtils.getRootCauseMessage(exception));
    }
}

