package org.appsugar.archetypes.security;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.appsugar.archetypes.permission.Permissions;
import org.appsugar.archetypes.security.jwt.JwtUserAdapter;
import org.springframework.security.core.Authentication;

import java.util.Base64;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.security
 * @className SecureBitExpressionRoot
 * @date 2021-07-08  14:04
 */
@Slf4j
public class BitSecureExpressionRoot extends AbstractMethodSecureExpressionRoot {
    protected BitSecure bitSecure;
    protected String permissionCode;
    @Getter
    @Setter
    protected Object filterObject;
    @Getter
    @Setter
    protected Object returnObject;

    protected Object thisObject;

    public BitSecureExpressionRoot(Authentication authentication, BitSecure bitSecure, Object thisObject) {
        super(authentication);
        this.bitSecure = bitSecure;
        this.permissionCode = bitSecure.value();
        this.thisObject = thisObject;
    }


    @Override
    public Object getThis() {
        return thisObject;
    }

    /**
     * 检测token中的bytearray是否存在指定的权限
     *
     * @return
     */
    public boolean hasPermissionByBit() {
        JwtUserAdapter loginUser = (JwtUserAdapter) authentication.getPrincipal();
        String base64Permission = loginUser.getJwtUser().getP();
        byte[] permissionByteArray = Base64.getDecoder().decode(base64Permission);
        return Permissions.checkPermission(permissionCode, permissionByteArray);
    }
}
