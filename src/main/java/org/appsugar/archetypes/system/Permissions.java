package org.appsugar.archetypes.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.permission
 * @className Permissions
 * @date 2021-06-14  13:44
 */
@Slf4j
public enum Permissions {
    SUPER_ADMIN("超级管理员", new Permission("超能力", "*")),
    USER("用户权限", new Permission("查看列表", "user:list"));
    //权限有改变时,modifyCount+1;
    public static final int modifyCount = 1;
    public static final List<Permissions> permissionGroups = Arrays.asList(Permissions.values());
    public static final Map<String, Integer> permissionBit = new HashMap<>();
    public static final Map<Integer, String> bitPermission = new HashMap<>();

    static {
        int i = 0;
        for (Permissions permission : permissionGroups) {
            for (Permission p : permission.permissions) {
                String code = p.code;
                permissionBit.put(code, i);
                bitPermission.put(i++, code);
            }
        }
    }

    /**
     * 检查bits中是否拥有code权限
     */
    public static boolean checkPermission(String code, byte[] bits) {
        BitSet b = BitSet.valueOf(bits);
        int bitIndex = permissionBit.getOrDefault(code, -1);
        if (bitIndex == -1) {
            //warning here  why permission code did not found
            log.warn("permission code {} not def ", code);
            return false;
        }
        return b.get(bitIndex);
    }

    /**
     * 把权限换算成位数组,减少前段数据库存储
     *
     * @param permissions
     * @return
     */
    public static byte[] permissionsToByteArray(Collection<String> permissions) {
        BitSet b = new BitSet(permissionBit.size());
        for (String permission : permissions) {
            int bitIndex = permissionBit.getOrDefault(permission, -1);
            if (bitIndex == -1) {
                continue;
            }
            b.set(bitIndex);
        }
        return b.toByteArray();
    }

    /**
     * 把数据位转换成对应权限列表
     *
     * @param bytes
     */
    public static List<String> byteArrayToPermissions(byte[] bytes) {
        int len = permissionBit.size();
        List<String> permissions = new ArrayList<>(len);
        BitSet b = BitSet.valueOf(bytes);
        for (int i = 0; i < len; i++) {
            if (b.get(i)) {
                String code = bitPermission.get(i);
                if (!StringUtils.isBlank(code)) {
                    permissions.add(code);
                }
            }
        }
        return permissions;
    }

    public static List<String> decodeStringPermission(String permissions) {
        if (StringUtils.isBlank(permissions)) {
            return new ArrayList<>();
        }
        return Arrays.stream(permissions.split(",")).collect(Collectors.toList());
    }

    public static String encodeToStringPermission(Collection<String> permissions) {
        return permissions.stream().collect(Collectors.joining(","));
    }

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
