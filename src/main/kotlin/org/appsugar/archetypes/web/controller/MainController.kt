package org.appsugar.archetypes.web.controller

import org.appsugar.archetypes.common.domain.Response
import org.appsugar.archetypes.extension.getLogger
import org.appsugar.archetypes.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
class MainController(val userRepository: UserRepository) {
    companion object {
        val logger = getLogger<MainController>()
    }

    @Autowired
    lateinit var authenticationManager: AuthenticationManager


    /**
     * 用户登录
     */
    @PostMapping("/login")
    fun login(username: String, password: String, request: HttpServletRequest): Response<Void> {
        val context = SecurityContextHolder.getContext()
        return try {
            val token = UsernamePasswordAuthenticationToken(username, password)
            context.authentication = authenticationManager.authenticate(token)!!
            Response.SUCCESS
        } catch (ex: AuthenticationException) {
            Response(-1, "用户名或账号密码错误")
        }
    }
}
