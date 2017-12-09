package org.appsugar.archetypes.web.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class MainController {

    @GetMapping("/login")
    fun login() = "login"

    @PostMapping("/login")
    fun loginFailure(model: Model) = model.addAttribute("msg", "账号或密码错误").let { "login" }

    @RequestMapping(value = ["/", "/index"])
    fun index() = "index"

    @GetMapping("/index1")
    fun index1() = "index"
}