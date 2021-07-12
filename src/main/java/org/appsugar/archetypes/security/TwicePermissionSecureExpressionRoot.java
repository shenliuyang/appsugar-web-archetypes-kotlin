package org.appsugar.archetypes.security;

import lombok.extern.slf4j.Slf4j;
import org.appsugar.archetypes.service.UserService;
import org.springframework.security.core.Authentication;

/**
 * 两次权限检测,第一次检测客户端上传权限表中是否有相关权限, 如果有, 再次检测该用户是否30分钟内登录. 如果是,那么在服务端进行权限检测(防止secure泄露导致超级admin) 反正要求客户端二次授权
 *
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.security
 * @className SecureBitExpressionRoot
 * @date 2021-07-08  14:04
 */
@Slf4j
public class TwicePermissionSecureExpressionRoot extends BitSecureExpressionRoot {
    public TwicePermissionSecureExpressionRoot(Authentication authentication, String permissionCode, Object thisObject, UserService userService) {
        super(authentication, permissionCode, thisObject, userService);
        this.userService = userService;
    }


    /**
     * 检测当前用户在服务端是否存在对应权限
     * 先检测token中是否拥有对应权限
     */
    public boolean hasPermissionByServer() {
        UserInfo loginUser = (UserInfo) authentication.getPrincipal();
        return super.hasPermissionByBit() ? userService.check(loginUser.token(), this.permissionCode) : false;
    }
}
