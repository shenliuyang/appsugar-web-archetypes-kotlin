package org.appsugar.archetypes.security.jwt;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * jwt拦截器, 处理当前用户信息, 处理过期登录信息
 *
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.security
 * @className JwtTokenFilter
 * @date 2021-07-07  16:06
 */
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    public static final String startWith = "Bearer ";
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Get authorization header and validate
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String bearer = startWith;
        if (StringUtils.isEmpty(header) || !header.startsWith(bearer)) {
            filterChain.doFilter(request, response);
            return;
        }
        // Get jwt token and validate, substring reduce memory allocate
        final String token = header.substring(bearer.length());
        if (!jwtTokenUtil.validate(token)) {
            filterChain.doFilter(request, response);
            return;
        }
        JwtUserAdapter user = jwtTokenUtil.getLoginUserFromToken(token);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
