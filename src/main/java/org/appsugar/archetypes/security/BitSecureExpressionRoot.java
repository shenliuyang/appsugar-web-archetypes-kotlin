package org.appsugar.archetypes.security;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.appsugar.archetypes.permission.Permissions;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.security
 * @className SecureBitExpressionRoot
 * @date 2021-07-08  14:04
 */
@Slf4j
public class BitSecureExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {
    protected BitSecure bitSecure;
    protected String permissionCode;
    @Getter
    @Setter
    protected Object filterObject;
    @Getter
    @Setter
    protected Object returnObject;

    protected Object thisObject;

    public BitSecureExpressionRoot(Authentication authentication, BitSecure bitSecure) {
        super(authentication);
        this.bitSecure = bitSecure;
        this.permissionCode = bitSecure.value();
    }


    public void setThis(Object thisObject) {
        this.thisObject = thisObject;
    }

    @Override
    public Object getThis() {
        return thisObject;
    }

    public boolean hasPermissionByBit() {
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        return Permissions.checkPermission(permissionCode, loginUser.getPermissionBitArray());
    }
}
