package org.appsugar.archetypes.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

/**
 * 权限数量有限,减少gc可以使用缓存
 *
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.security
 * @className StringGrantedAuthority
 * @date 2021-07-07  16:18
 */
@Data
public class StringGrantedAuthority implements GrantedAuthority {
    private String s;

    public StringGrantedAuthority(String s) {
        this.s = s;
    }

    public StringGrantedAuthority() {
    }

    @Override
    public String getAuthority() {
        return s;
    }
}
