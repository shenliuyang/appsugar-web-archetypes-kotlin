package org.appsugar.archetypes.web.controller

import org.apache.shiro.authz.AuthorizationException
import org.appsugar.archetypes.extension.attr
import org.appsugar.archetypes.extension.getLogger
import org.appsugar.archetypes.web.security.Permission
import org.appsugar.archetypes.web.security.ShiroUtils
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ModelAttribute
import javax.servlet.http.HttpServletRequest


@org.springframework.web.bind.annotation.ControllerAdvice
class ControllerAdvice {
    companion object {
        val logger = getLogger<ControllerAdvice>()
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