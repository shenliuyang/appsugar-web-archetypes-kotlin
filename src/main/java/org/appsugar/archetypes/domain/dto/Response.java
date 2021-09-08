package org.appsugar.archetypes.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.domain.dto
 * @className Response
 * @date 2021-07-07  17:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    public static final int UN_AUTHENTICATION_CODE = -200;
    public static final int RE_AUTHENTICATION_CODE = -201;
    public static final int RE_AUTHORIZATION_CODE = -202;
    public static final int UN_AUTHORIZATION_CODE = -203;

    public static final Response UN_AUTHENTICATION = new Response(UN_AUTHENTICATION_CODE, "请先登录");
    public static final Response RE_AUTHENTICATION = new Response(RE_AUTHENTICATION_CODE, "授权信息已过期,请重新授权");
    public static final Response RE_AUTHORIZATION = new Response(RE_AUTHORIZATION_CODE, "需要您二次授权认证");
    public static final Response UN_AUTHORIZATION = new Response(UN_AUTHORIZATION_CODE, "您的权限不足");

    private int code = 0;
    private String msg = "success";


    public static Response error(String msg) {
        return error(-1, msg);
    }

    public static Response error(int code, String msg) {
        return new Response(code, msg);
    }
}
