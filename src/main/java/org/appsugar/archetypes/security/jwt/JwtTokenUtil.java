package org.appsugar.archetypes.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.security
 * @className JwtUtil
 * @date 2021-07-07  16:09
 */
//@Component
//@ConfigurationProperties("spring.jwt.secret")
public class JwtTokenUtil {
    @Getter
    @Setter
    private String secret = "test";

    public boolean validate(String token) {
        return Jwts.parser()
                .setSigningKey(secret).isSigned(token);
    }

    public JwtUser getLoginUserFromToken(String token) {
        JwtUser jwtUser = JwtUser.fromMap(Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody());
        return jwtUser;
    }

    public String generateAccessToken(JwtUser user) {
        String token = Jwts.builder()
                .setClaims(user.toMap())
                .signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }
}
