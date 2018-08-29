package org.appsugar.archetypes.web.security

enum class Permission(
        val value: String, val code: String
) {
    ADMIN_ALL("*", "超级管理员"),
    USER_VIEW("user:view", "用户查看"), USER_EDIT("user:edit", "用户编辑"), USER_DELETE("user:delete", "用户删除"),
    ROLE_VIEW("role:view", "角色查看"), ROLE_EDIT("role:edit", "角色编辑"), ROLE_DELETE("role:delete", "角色删除");

    companion object {
        val GROUP_BY_PREFIX = Permission.values().groupBy { it.name.substringBefore("_") }
        val GROUP_BY_VALUE = Permission.values().associateBy { it.value }

        fun getPermissionDtoGroupByPrefix(): MutableList<PermissionGroupDto> {
            var permissionDtoGroupByPrefix = mutableListOf<PermissionGroupDto>()
            GROUP_BY_PREFIX.forEach { k, v ->
                permissionDtoGroupByPrefix.add(PermissionGroupDto(k, v.map {
                    PermissionDto(it.value, it.code)
                }.toList()))
            }
            return permissionDtoGroupByPrefix
        }

        fun permissionMap(): MutableMap<String, String> {
            val mutableMap = mutableMapOf<String, String>()
            return Permission.values().forEach { mutableMap[it.value] = it.code }.let { mutableMap }
        }
    }
}

data class PermissionDto(val value: String, val code: String)

data class PermissionGroupDto(val code: String, val permissions: List<PermissionDto>)