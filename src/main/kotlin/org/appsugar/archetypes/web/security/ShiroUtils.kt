package org.appsugar.archetypes.web.security

import org.apache.shiro.SecurityUtils

class ShiroUtils {
    companion object {
        /**
         * 获取当前用户
         */
        fun getSubject() = SecurityUtils.getSubject()

        /**
         * 获取当前用户信息
         */
        fun getPrincipal(): Principal {
            return getSubject().principal as Principal
        }
    }
}