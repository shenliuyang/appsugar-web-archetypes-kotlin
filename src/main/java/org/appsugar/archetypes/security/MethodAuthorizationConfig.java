package org.appsugar.archetypes.security;

import org.aopalliance.intercept.MethodInvocation;
import org.appsugar.archetypes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.Authentication;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.security
 * @className MethodAuthConfig
 * @date 2021-07-08  14:00
 */
//@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, proxyTargetClass = true)
public class MethodAuthorizationConfig extends GlobalMethodSecurityConfiguration {
    @Autowired
    private UserService userService;

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return new DefaultMethodSecurityExpressionHandler() {
            @Override
            protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
                AbstractMethodSecureExpressionRoot root = null;
                BitSecure bitSecure = invocation.getMethod().getAnnotation(BitSecure.class);
                TwiceSecure twiceSecure = invocation.getMethod().getAnnotation(TwiceSecure.class);
                if (bitSecure != null) {
                    root = new BitSecureExpressionRoot(authentication, bitSecure.value(), invocation.getThis(), userService);
                } else if (twiceSecure != null) {
                    root = new TwicePermissionSecureExpressionRoot(authentication, twiceSecure.value(), invocation.getThis(), userService);
                } else {
                    return super.createSecurityExpressionRoot(authentication, invocation);
                }
                root.setPermissionEvaluator(getPermissionEvaluator());
                root.setTrustResolver(getTrustResolver());
                root.setRoleHierarchy(getRoleHierarchy());
                return root;
            }
        };
    }
}
