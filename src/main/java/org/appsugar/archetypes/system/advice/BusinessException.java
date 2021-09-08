package org.appsugar.archetypes.system.advice;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.system.advice
 * @className BusinessException
 * @date 2021-09-08  09:06
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
