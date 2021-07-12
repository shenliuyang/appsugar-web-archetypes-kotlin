package org.appsugar.archetypes.security;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.appsugar.archetypes.service.UserService;
import org.springframework.security.core.Authentication;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.security
 * @className SecureBitExpressionRoot
 * @date 2021-07-08  14:04
 */
@Slf4j
public class BitSecureExpressionRoot extends AbstractMethodSecureExpressionRoot {
    protected String permissionCode;
    @Getter
    @Setter
    protected Object filterObject;
    @Getter
    @Setter
    protected Object returnObject;

    protected Object thisObject;

    protected UserService userService;

    public BitSecureExpressionRoot(Authentication authentication, String permissionCode, Object thisObject, UserService userService) {
        super(authentication);
        this.permissionCode = permissionCode;
        this.thisObject = thisObject;
        this.userService = userService;
    }


    @Override
    public Object getThis() {
        return thisObject;
    }

    /**
     * 检测token中的bytearray是否存在指定的权限
     */
    public boolean hasPermissionByBit() {
        UserInfo u = (UserInfo) authentication.getPrincipal();
        return userService.bitCheck(u.permissionModifyCount(), permissionCode, u.permissionByteArray());
    }
}
