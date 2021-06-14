package org.appsugar.archetypes.permission;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.permission
 * @className Permissions
 * @date 2021-06-14  13:44
 */
public enum Permissions {
    SUPER_ADMIN("超级管理员", new Permission("超能力", "*")),
    USER("用户权限", new Permission("查看列表", "user:list"));
    private String groupName;
    private Permission[] permissions;

    Permissions(String groupName, Permission... permissions) {
        this.groupName = groupName;
        this.permissions = permissions;
    }

    public String getGroupName() {
        return groupName;
    }

    public Permission[] getPermissions() {
        return permissions;
    }

    @Data
    @AllArgsConstructor
    public static class Permission {
        private String name;
        private String code;
    }
}
