package org.appsugar.archetypes.security;

/**
 * 授权异常
 *
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.security
 * @className AuthorizationExceptions
 * @date 2021-07-11  20:49
 */
public class AuthorizationExceptions {
    public static final AuthorizationExpiredException AUTHORIZATION_EXPIRED_EXCEPTION = new AuthorizationExpiredException();
    public static final ReAuthorizationException RE_AUTHORIZATION_EXCEPTION = new ReAuthorizationException();

    /**
     * 权限已过期,需要重新授权
     */
    public static class AuthorizationExpiredException extends RuntimeException {
        public AuthorizationExpiredException() {
        }

        public AuthorizationExpiredException(String message) {
            super(message);
        }
    }

    /**
     * 需要二次授权
     */
    public static class ReAuthorizationException extends RuntimeException {
        public ReAuthorizationException() {
        }

        public ReAuthorizationException(String message) {
            super(message);
        }
    }
}
