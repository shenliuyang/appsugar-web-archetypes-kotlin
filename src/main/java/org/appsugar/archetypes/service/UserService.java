package org.appsugar.archetypes.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.appsugar.archetypes.security.AuthorizationExceptions;
import org.appsugar.archetypes.security.LoginUser;
import org.appsugar.archetypes.system.Permissions;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.service
 * @className UserService
 * @date 2021-07-11  18:37
 */
@Service
@Slf4j
public class UserService {
    //集群模式下,需使用redis
    Cache<String, LoginUser> cache = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build();

    /**
     * 生成用户登录token 并暂存30分钟(30分钟内无需二次授权操作)
     */
    public String generateUserToken(LoginUser loginUser) {
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        cache.put(token, loginUser);
        log.debug("给用户 {} 生成唯一token {}", loginUser.getUserId(), token);
        return token;
    }

    /**
     * 查看对应token用户是否拥有相应权限
     */
    public boolean check(String token, String permissionCode) {
        LoginUser loginUser = cache.getIfPresent(token);
        if (loginUser == null) {
            //用户缓存信息不存在,需要二次授权
            throw AuthorizationExceptions.RE_AUTHORIZATION_EXCEPTION;
        }
        log.debug("检测用户{} 是否存在权限{}", loginUser.getUserId(), permissionCode);
        return loginUser.getPermissions().contains(permissionCode);
    }

    /**
     * 查看用户是否用相应权限
     */
    public boolean bitCheck(int permissionModifyCount, String permissionCode, byte[] permissionByteArray) {
        if (permissionModifyCount != Permissions.modifyCount) {
            throw AuthorizationExceptions.AUTHORIZATION_EXPIRED_EXCEPTION;
        }
        return Permissions.checkPermission(permissionCode, permissionByteArray);
    }


}
