package org.appsugar.archetypes.web.controller

import org.appsugar.archetypes.web.security.Permission
import org.appsugar.archetypes.web.security.ShiroUtils
import org.springframework.web.bind.annotation.ModelAttribute


@org.springframework.web.bind.annotation.ControllerAdvice
class ControllerAdvice{
    @ModelAttribute("menus")
    fun menus()=menus
}


class Menu(val name:String="", val url:String="", val permission:String="", val children:List<Menu> = emptyList()){
     /**
      * 是否拥有查看该菜单的权限
      */
     fun hasPermission():Boolean=if(children.isEmpty())ShiroUtils.getSubject().isPermitted(permission) else children.indexOfFirst {it.hasPermission()}!=-1
}

val menus = listOf<Menu>(
        Menu(name="系统管理",children =  listOf(
                Menu("用户管理","/system/user/list",Permission.USER_VIEW.value)
        ))
)