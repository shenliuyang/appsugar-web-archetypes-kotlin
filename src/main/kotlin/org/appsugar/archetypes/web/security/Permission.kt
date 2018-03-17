package org.appsugar.archetypes.web.security

enum class Permission(
        val value: String, val code: String, val dependencies: List<Permission> = emptyList()
) {
    ADMIN_ALL("*", "管理员"),
    USER_ALL("user:*", "所有权限"), USER_VIEW("user:view", "用户查看"), USER_EDIT("user:edit", "用户编辑"), USER_DELETE("user:delete", "用户删除"),
    ROLE_ALL("role:*", "所有权限"), ROLE_VIEW("role:view", "角色查看"), ROLE_EDIT("role:edit", "角色编辑"), ROLE_DELETE("role:delete", "角色删除"),
    ORG_ALL("org:*", "所有权限"), ORG_VIEW("org:view", "机构查看"), ORG_EDIT("org:edit", "机构编辑"), ORG_DELETE("org:delete", "机构删除");

    companion object {
        val GROUP_BY_PREFIX = Permission.values().groupBy { it.name.substringBefore("_") }
        val GROUP_BY_VALUE = Permission.values().associateBy { it.value }
    }
}