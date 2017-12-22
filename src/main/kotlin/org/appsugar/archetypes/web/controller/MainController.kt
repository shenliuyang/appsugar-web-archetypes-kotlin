package org.appsugar.archetypes.web.controller

import org.appsugar.archetypes.repository.RoleRepository
import org.appsugar.archetypes.repository.UserRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class MainController(val roleRepository: RoleRepository, val userRepository: UserRepository) {

    @GetMapping("/login")
    fun login(): String {
        return "login"
    }

    @PostMapping("/login")
    fun loginFailure(model: Model) = model.addAttribute("msg", "账号或密码错误").let { "login" }

    @RequestMapping(value = ["/", "/index"])
    fun index() = "index"

}
