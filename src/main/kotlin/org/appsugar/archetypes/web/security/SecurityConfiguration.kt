package org.appsugar.archetypes.web.security


import com.hazelcast.core.HazelcastInstance
import org.apache.shiro.SecurityUtils
import org.apache.shiro.cache.CacheManager
import org.apache.shiro.hazelcast.cache.HazelcastCacheManager
import org.apache.shiro.mgt.SecurityManager
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor
import org.apache.shiro.spring.web.ShiroFilterFactoryBean
import org.apache.shiro.web.mgt.DefaultWebSecurityManager
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class SecurityConfiguration {

    @Bean
    fun shiroFilterFactoryBean(securityManager: SecurityManager) = ShiroFilterFactoryBean().apply {
        loginUrl = "/login"
        successUrl = "/index"
        this.securityManager = securityManager
        filterChainDefinitionMap = mapOf("/login" to "authc", "/logout" to "logout"
                , "/static/**" to "anon", "/favicon.ico" to "anon", "/webjars/**" to "anon", "/**" to "user")
    }


    @Bean
    fun securityManager(shiroCacheManager: CacheManager) = DefaultWebSecurityManager().apply {
        realms = listOf(realm())
        cacheManager = shiroCacheManager
        sessionManager = DefaultWebSessionManager().apply {
            isSessionValidationSchedulerEnabled = false
            sessionDAO = EnterpriseCacheSessionDAO()
        }
        SecurityUtils.setSecurityManager(this)//make sure always hava a securityManager
    }

    @Bean
    fun realm() = ShiroRealm()

    @Bean
    fun shiroCacheManager(instance: HazelcastInstance) = HazelcastCacheManager().apply { hazelcastInstance = instance }

    @Bean
    fun authorizer() = AuthorizationAttributeSourceAdvisor()

    /**fix 404 not found ,  default proxy is jdk dynamic proxy**/
    @Bean
    fun defaultAdvisorAutoProxyCreator() = DefaultAdvisorAutoProxyCreator().apply { isProxyTargetClass = true }


}