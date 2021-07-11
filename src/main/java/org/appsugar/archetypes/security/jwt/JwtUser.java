package org.appsugar.archetypes.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * jwt 认证用户信息, 不能够存储敏感信息(对客户端可见)
 *
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.security.jwt
 * @className JwtUser
 * @date 2021-07-11  18:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtUser {
    private static ObjectMapper om = new ObjectMapper();
    //用户id
    private Long i;
    //用户权限
    private String p;
    //用户权限状态号
    private int pm;
    //后端用户映射token值
    private String tk;


    public Map<String, Object> toMap() {
        return om.convertValue(this, Map.class);
    }

    public static JwtUser fromMap(Map<String, Object> map) {
        return om.convertValue(map, JwtUser.class);
    }
}
