package org.appsugar.archetypes.web.security

import org.apache.shiro.SecurityUtils
import org.apache.shiro.subject.Subject

class ShiroUtils {
    companion object {
        /**
         * 获取当前用户
         */
        fun getSubject(): Subject = SecurityUtils.getSubject()

        /**
         * 获取当前用户信息
         */
        fun getPrincipal(): Principal {
            return getSubject().principal as Principal
        }
    }
}