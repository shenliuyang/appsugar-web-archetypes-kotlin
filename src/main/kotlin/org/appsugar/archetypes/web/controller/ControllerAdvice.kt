package org.appsugar.archetypes.web.controller

import org.appsugar.archetypes.common.domain.Response
import org.appsugar.archetypes.extension.getLogger
import org.springframework.security.access.AccessDeniedException
import org.springframework.ui.Model
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.RestControllerAdvice
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
    fun handleUnAuthrizationException(model: Model) = Response.UN_AUTHROIZED


    /**
     * 处理系统异常
     */
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): Response<Void> {
        logger.error("some error occurred", ex)
        val sb = StringBuilder()
        var root: Throwable? = ex
        do {
            root?.let {
                sb.append("${it.message} ")
                root = it.cause
            }
        } while (root != null)
        return Response.error(sb.toString())
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
