package org.appsugar.archetypes.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.security
 * @className JwtUtil
 * @date 2021-07-07  16:09
 */
@Component
public class JwtTokenUtil {
    private String secret = "test";

    public boolean validate(String token) {
        return Jwts.parser()
                .setSigningKey(secret).isSigned(token);
    }

    public LoginUser getLoginUserFromToken(String token) {
        return LoginUser.fromMap(Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody());
    }

    public String generateAccessToken(LoginUser user) {
        String token = Jwts.builder()
                .setClaims(user.toMap())
                .signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }
}
