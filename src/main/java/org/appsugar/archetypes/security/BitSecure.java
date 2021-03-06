package org.appsugar.archetypes.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 位授权方式,检测客户端传上来的byte数组对应到权限表中. 是否存在该权限 (应为是客户端传上来,jwt 秘钥有可能被盗,所以危险操作需要二次授权)
 *
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.security
 * @className BitSecure
 * @date 2021-07-08  14:02
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasPermissionByBit()")
public @interface BitSecure {
    /**
     * 权限
     */
    String value();
}
