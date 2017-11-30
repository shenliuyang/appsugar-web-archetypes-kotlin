package org.appsugar.archetypes.web.controller

import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.UsernamePasswordToken
import org.appsugar.archetypes.extension.getLogger
import org.appsugar.archetypes.web.security.ShiroUtils
import org.appsugar.bean.domain.Response
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
class MainController{

    @GetMapping("/login")
    fun login()="login"

    @PostMapping("/login")
    fun loginFailure(model:Model)=model.addAttribute("msg","账号或密码错误").let { "login" }


    @RequestMapping(value=["/","/index"])
    fun index()="index"
}