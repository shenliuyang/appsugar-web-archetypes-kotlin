package org.appsugar.archetypes.web.security

enum class Permission(
        val value:String,val code:String,val dependencies:List<Permission> = emptyList()
){
    ADMIN_ALL("*","所有权限"),
    USER_ALL("user:*","用户所有权限"),USER_VIEW("user:view","用户查看"),USER_EDIT("user:edit","用户编辑"),USER_DELETE("user:delete","用户删除")
}