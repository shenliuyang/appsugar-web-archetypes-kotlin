package org.appsugar.archetypes.security;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.appsugar.archetypes.security.jwt.JwtUserAdapter;
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
public class TwicePermissionSecureExpressionRoot extends AbstractMethodSecureExpressionRoot {
    protected TwiceSecure bitSecure;
    protected String permissionCode;
    @Getter
    @Setter
    protected Object filterObject;
    @Getter
    @Setter
    protected Object returnObject;

    protected Object thisObject;

    protected UserService userService;

    public TwicePermissionSecureExpressionRoot(Authentication authentication, TwiceSecure twicePermissionCheck, Object thisObject, UserService userService) {
        super(authentication);
        this.bitSecure = twicePermissionCheck;
        this.permissionCode = twicePermissionCheck.value();
        this.thisObject = thisObject;
        this.userService = userService;
    }


    public void setThis(Object thisObject) {
        this.thisObject = thisObject;
    }

    @Override
    public Object getThis() {
        return thisObject;
    }

    /**
     * 检测当前用户在服务端是否存在对应权限
     */
    public boolean hasPermissionByServer() {
        JwtUserAdapter loginUser = (JwtUserAdapter) authentication.getPrincipal();
        return userService.check(loginUser.getJwtUser().getTk(), this.permissionCode);
    }
}
