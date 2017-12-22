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
    fun shiroFilterFactoryBean(securityManager: SecurityManager) = with(ShiroFilterFactoryBean())
    {
        loginUrl = "/login"
        successUrl = "/index"
        this.securityManager = securityManager
        filterChainDefinitionMap = mapOf("/login" to "authc", "/logout" to "logout"
                , "/static/**" to "anon", "/favicon.ico" to "anon", "/webjars/**" to "anon", "/**" to "user")
        this
    }


    @Bean
    fun securityManager(cacheManager: CacheManager) = with(DefaultWebSecurityManager()) {
        this.realms = listOf(realm())
        this.cacheManager = cacheManager
        this.sessionManager = with(DefaultWebSessionManager()) {
            isSessionValidationSchedulerEnabled = false
            sessionDAO = EnterpriseCacheSessionDAO()
            this
        }
        SecurityUtils.setSecurityManager(this)//make sure always hava a securityManager
        this
    }

    @Bean
    fun realm() = ShiroRealm()

    @Bean
    fun cacheManager(instance: HazelcastInstance) = with(HazelcastCacheManager()) {
        this.hazelcastInstance = instance
        this
    }

    @Bean
    fun authorizer() = AuthorizationAttributeSourceAdvisor()

    /**fix 404 not found ,  default proxy is jdk dynamic proxy**/
    @Bean
    fun defaultAdvisorAutoProxyCreator() = DefaultAdvisorAutoProxyCreator().let { it.isProxyTargetClass = true;it }


}