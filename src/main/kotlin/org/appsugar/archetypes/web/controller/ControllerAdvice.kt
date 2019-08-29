package org.appsugar.archetypes.web.controller

import org.appsugar.archetypes.common.domain.Response
import org.appsugar.archetypes.util.getLogger
import org.slf4j.MDC
import org.springframework.security.access.AccessDeniedException
import org.springframework.ui.Model
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.beans.PropertyEditorSupport
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@RestControllerAdvice
class ControllerAdvice {
    companion object {
        val logger = getLogger<ControllerAdvice>()
        val LOCAL_DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")!!
        val LOCAL_DATE_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd")!!
    }


    /**
     * 处理权限不够异常
     */
    @ExceptionHandler(AccessDeniedException::class)
    fun handleUnAuthrizationException(model: Model) = Mono.just(Response.UN_AUTHORIZED)


    val urlKey = "url"
    val methodKey = "method"
    /**
     * 处理系统异常
     */
    @ExceptionHandler(Exception::class)
    fun handleException(serverWebExchange: ServerWebExchange, ex: Exception): Mono<Response<Void>> {
        val req = serverWebExchange.request
        MDC.put(urlKey, req.uri.toString())
        MDC.put(methodKey, req.methodValue)
        logger.error("controller  error", ex)
        MDC.remove(urlKey)
        MDC.remove(methodKey)
        val sb = StringBuilder()
        var root: Throwable? = ex
        do {
            root?.let {
                sb.append("${it.message} ")
                root = it.cause
            }
        } while (root != null)
        return Mono.just(Response.error(sb.toString()))
    }


    @InitBinder
    fun initWebBinder(webDataBinder: WebDataBinder) {
        webDataBinder.registerCustomEditor(LocalDateTime::class.java, object : PropertyEditorSupport() {
            override fun setAsText(text: String) {
                value = LocalDateTime.parse(text, LOCAL_DATE_TIME_PATTERN)
            }
        })

        webDataBinder.registerCustomEditor(LocalDate::class.java, object : PropertyEditorSupport() {
            override fun setAsText(text: String) {
                value = LocalDate.parse(text, LOCAL_DATE_PATTERN)
            }
        })

    }
}
