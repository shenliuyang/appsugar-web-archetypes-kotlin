package org.appsugar.archetypes.web.controller

import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.UsernamePasswordToken
import org.appsugar.archetypes.extension.getLogger
import org.appsugar.archetypes.web.security.ShiroUtils
import org.appsugar.bean.domain.Response
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@Controller
class MainController{

    private companion object {
        val LOGIN_ERROR_RESPONSE=Response.error("username or password error") as Response<Void>
        val logger =  getLogger<MainController>()
    }

    @ResponseBody
    @RequestMapping("/login")
    fun login(username:String,password:String):Response<Void>{
        val subject = ShiroUtils.getSubject()
        if(subject.isAuthenticated){
            subject.logout()
        }
        return try {
            subject.login(UsernamePasswordToken(username,password,false))
            Response.SUCCESS as Response<Void>
        }catch (e: AuthenticationException){
            MainController.LOGIN_ERROR_RESPONSE
        }catch(e:Exception){
            logger.error("user login error ", e)
            Response.error("username or password error")
        }
    }

    @RequestMapping("/greeting")
    fun hello(model:Model):String{
        model.addAttribute("name","Hello World1")
        return "greeting"
    }
}