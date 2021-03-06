package org.appsugar.archetypes.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.appsugar.archetypes.security.UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.security.jwt
 * @className JwtUserAdapter
 * @date 2021-07-11  18:33
 */
@Data
@AllArgsConstructor
public class JwtUserAdapter implements UserDetails, UserInfo {
    private JwtUser jwtUser;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
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

    @Override
    public long id() {
        return jwtUser.getI();
    }

    @Override
    public byte[] permissionByteArray() {
        return jwtUser.getPermissionByteArray();
    }

    @Override
    public int permissionModifyCount() {
        return jwtUser.getPm();
    }

    @Override
    public String token() {
        return jwtUser.getTk();
    }

    @Override
    public long createAt() {
        return jwtUser.getCt();
    }
}
