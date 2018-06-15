package org.appsugar.archetypes.web.controller

import org.apache.shiro.authc.UsernamePasswordToken
import org.appsugar.archetypes.common.domain.Response
import org.appsugar.archetypes.extension.getLogger
import org.appsugar.archetypes.repository.RoleRepository
import org.appsugar.archetypes.repository.UserRepository
import org.appsugar.archetypes.web.security.ShiroUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
class MainController(val roleRepository: RoleRepository, val userRepository: UserRepository) {
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

    @RequestMapping("logout")
    fun logout(): Response {
        ShiroUtils.getSubject().logout()
        return Response.SUCCESS
    }


}
