package org.appsugar.archetypes.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class Response<T> {
    public static final int UN_AUTHENTICATION_CODE = -200;
    public static final int RE_AUTHENTICATION_CODE = -201;
    public static final int RE_AUTHORIZATION_CODE = -202;
    public static final int UN_AUTHORIZATION_CODE = -203;

    public static final Response<Void> UN_AUTHENTICATION = new Response<>(null, UN_AUTHENTICATION_CODE, "请先登录");
    public static final Response<Void> RE_AUTHENTICATION = new Response<>(null, RE_AUTHENTICATION_CODE, "授权信息已过期,请重新授权");
    public static final Response<Void> RE_AUTHORIZATION = new Response<>(null, RE_AUTHORIZATION_CODE, "需要您二次授权认证");
    public static final Response<Void> UN_AUTHORIZATION = new Response<>(null, UN_AUTHORIZATION_CODE, "您的权限不足");

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object result = null;
    //0正常,其他异常
    private int code = 0;
    private String msg = "success";

    public static <T> Response<T> success(T object) {
        return new Response<>(object, 0, "success");
    }

    public static Response<Void> error(String msg) {
        return error(-1, msg);
    }

    public static Response<Void> error(int code, String msg) {
        return new Response<>(null, code, msg);
    }
}
