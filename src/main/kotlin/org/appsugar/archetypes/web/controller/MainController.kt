package org.appsugar.archetypes.web.controller

import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.appsugar.archetypes.common.domain.Response
import org.appsugar.archetypes.entity.User
import org.appsugar.archetypes.extension.getLogger
import org.appsugar.archetypes.repository.UserRepository
import org.appsugar.archetypes.web.security.ShiroUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.Serializable
import javax.servlet.http.HttpServletRequest

@RestController
class MainController(val userRepository: UserRepository) {
    companion object {
        val logger = getLogger<MainController>()
    }

    @RequestMapping("/login")
    fun login(username: String, password: String, rememberMe: Boolean?, request: HttpServletRequest): Response {
        val subject = ShiroUtils.getSubject()
        return if (subject.isAuthenticated) Response.error("already login") else try {

            subject.login(UsernamePasswordToken(username, password.toCharArray(), rememberMe ?: false))
            Response.SUCCESS
        } catch (ex: Exception) {
            logger.error("user login error {}", username, ex)
            Response.error("Username or password error")
        }
    }

    @RequiresAuthentication
    @GetMapping("/me")
    fun me(): Response {
        //获取当前登录用户
        val principal = ShiroUtils.getPrincipal()
        val user = User()
        val loginUser = userRepository.findById(principal.id).get()
        var permissionList = user.permissions.toMutableList()
        loginUser.roles.forEach {
            val list = it.permissions
            permissionList.addAll(list)
        }
        permissionList = permissionList.distinct().toMutableList()
        val responseData = object:Serializable {
            var baseInfo = user
            var permissions =permissionList
        }
        return Response(200, "success", responseData)
    }

    @RequestMapping("logout")
    fun logout(): Response {
        ShiroUtils.getSubject().logout()
        return Response.SUCCESS
    }


}
