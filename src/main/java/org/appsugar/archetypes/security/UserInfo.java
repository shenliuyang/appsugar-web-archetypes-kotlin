package org.appsugar.archetypes.security;

/**
 * 用户登录信息
 *
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.security
 * @className UserInfo
 * @date 2021-07-12  09:43
 */
public interface UserInfo {
    long id();

    byte[] permissionByteArray();

    int permissionModifyCount();

    String token();

    long createAt();
}
