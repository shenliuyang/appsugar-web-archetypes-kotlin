package org.appsugar.archetypes.security;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.security
 * @className AbstractMethodSecureExpressionRoot
 * @date 2021-07-11  18:05
 */
public abstract class AbstractMethodSecureExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    public AbstractMethodSecureExpressionRoot(Authentication authentication) {
        super(authentication);
    }
}
