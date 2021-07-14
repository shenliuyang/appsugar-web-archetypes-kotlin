package org.appsugar.archetypes.security.jwt;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.security.jwt
 * @className RequestUser
 * @date 2021-07-12  11:15
 */
@Data
public class RequestUser {
    @NotBlank(message = "账号不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
}
