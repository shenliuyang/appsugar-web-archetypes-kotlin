package org.appsugar.archetypes.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.appsugar.archetypes.security.LoginUser;
import org.appsugar.archetypes.security.UserInfo;
import org.appsugar.archetypes.system.Permissions;

import java.util.Base64;
import java.util.Map;

/**
 * jwt 认证用户信息, 不能够存储敏感信息(对客户端可见)
 *
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.security.jwt
 * @className JwtUser
 * @date 2021-07-11  18:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtUser implements UserInfo {
    private static ObjectMapper om = new ObjectMapper();
    //用户id
    private long i;
    //用户权限
    private String p;
    //用户权限状态号
    private int pm;
    //后端用户映射token值
    private String tk;
    //创建时间
    private long ct;


    public Map<String, Object> toMap() {
        return om.convertValue(this, Map.class);
    }

    public static JwtUser fromMap(Map<String, Object> map) {
        return om.convertValue(map, JwtUser.class);
    }

    public static JwtUser fromLoginUser(LoginUser loginUser, String token) {
        JwtUser jwtUser = new JwtUser();
        jwtUser.setI(loginUser.getUserId());
        jwtUser.setP(Base64.getEncoder().encodeToString(Permissions.permissionsToByteArray(loginUser.getPermissions())));
        jwtUser.setPm(loginUser.getPermissionModifyCount());
        jwtUser.setTk(token);
        jwtUser.setCt(loginUser.getLoginAt());
        return jwtUser;
    }

    private byte[] getPermissionByteArray() {
        return Base64.getDecoder().decode(p);
    }

    @Override
    public long id() {
        return i;
    }

    @Override
    public byte[] permissionByteArray() {
        return getPermissionByteArray();
    }

    @Override
    public int permissionModifyCount() {
        return pm;
    }

    @Override
    public String token() {
        return tk;
    }

    @Override
    public long createAt() {
        return ct;
    }
}
