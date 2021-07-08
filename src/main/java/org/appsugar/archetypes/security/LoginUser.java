package org.appsugar.archetypes.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.appsugar.archetypes.permission.Permissions;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户登录信息
 *
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.security
 * @className LoginUser
 * @date 2021-07-07  14:41
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginUser implements UserDetails {
    private Long userId;
    private String username;
    private String password;
    private byte[] permissionBitArray;
    private int permissionModifyCount;


    public Map<String, Object> toMap() {
        Map<String, Object> claims = new HashMap<>();
        byte[] bits = permissionBitArray;
        claims.put("p", Base64.getEncoder().encodeToString(bits));
        claims.put("i", userId);
        claims.put("pm", permissionModifyCount);
        return claims;
    }

    public static LoginUser fromMap(Map<String, Object> map) {
        LoginUser u = new LoginUser();
        u.userId = ((Number) map.get("i")).longValue();
        String permissionBits = (String) map.get("p");
        u.permissionModifyCount = (Integer) map.get("pm");
        //客户端授权跟服务器授权不匹配时,需要重新授权 TODO 抛出自定义异常
        if (u.permissionModifyCount != Permissions.modifyCount) {
            throw new IllegalArgumentException("client authorization expired  current system state is " + Permissions.modifyCount + " client state is " + u.permissionModifyCount);
        }
        byte[] bits = Base64.getDecoder().decode(permissionBits);
        u.permissionBitArray = bits;
        u.permissionModifyCount = Permissions.modifyCount;
        return u;
    }

    /**
     * 不再提供普通授权
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
