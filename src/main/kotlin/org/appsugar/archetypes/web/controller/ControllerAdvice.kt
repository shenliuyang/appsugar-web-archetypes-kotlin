package org.appsugar.archetypes.web.controller

import org.apache.shiro.authz.AuthorizationException
import org.appsugar.archetypes.extension.attr
import org.appsugar.archetypes.extension.getLogger
import org.appsugar.archetypes.web.security.Permission
import org.appsugar.archetypes.web.security.ShiroUtils
import org.springframework.ui.Model
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.ModelAttribute
import java.beans.PropertyEditorSupport
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpServletRequest


@org.springframework.web.bind.annotation.ControllerAdvice
class ControllerAdvice {
    companion object {
        val logger = getLogger<ControllerAdvice>()
        val LOCAL_DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")!!
        val LOCAL_DATE_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd")!!
        val TIME_PATTERN = DateTimeFormatter.ofPattern("HH:mm:ss")
    }

    @ModelAttribute("menus")
    fun menus() = menus

    /**check permission with shiro  in thymeleaf**/
    @ModelAttribute("shiro")
    fun subject() = ShiroUtils.getSubject()

    /**
     * 处理权限不够异常
     */
    @ExceptionHandler(AuthorizationException::class)
    fun handleUnAuthrizationException(model: Model) = model.addMenus().let { "error/403.html" }


    /**
     * 处理系统异常
     */
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception, req: HttpServletRequest, model: Model): String {
        logger.error("|${ShiroUtils.getPrincipal().id}|${req.remoteHost}|${req.requestURI}|", ex)
        val sb = StringBuilder(" 请求 ${req.requestURI} 发生异常: ")
        var root: Throwable? = ex
        do {
            root?.let {
                sb.append("|${it.message}")
                root = it.cause
            }
        } while (root != null)
        sb.append("|")
        model.attr("msg", sb.toString())
        model.addMenus()
        return "error/500.html"
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

        webDataBinder.registerCustomEditor(LocalTime::class.java, object : PropertyEditorSupport() {
            override fun setAsText(text: String) {
                value = LocalTime.parse(text, TIME_PATTERN)
            }
        })
    }

    fun Model.addMenus() = this.attr("menus", menus)
}

class Menu(val name: String = "", val url: String = "", val permission: String = "", val children: List<Menu> = emptyList()) {
    /**
     * 是否拥有查看该菜单的权限
     */
    fun hasPermission(): Boolean = if (children.isEmpty()) ShiroUtils.getSubject().isPermitted(permission) else children.indexOfFirst { it.hasPermission() } != -1
}

val menus = listOf<Menu>(
        Menu(name = "系统管理", children = listOf(
                Menu("角色管理", "/system/role", Permission.ROLE_VIEW.value),
                Menu("用户管理", "/system/user", Permission.USER_VIEW.value),
                Menu("机构管理", "/system/org", Permission.ORG_VIEW.value)
        ))
)