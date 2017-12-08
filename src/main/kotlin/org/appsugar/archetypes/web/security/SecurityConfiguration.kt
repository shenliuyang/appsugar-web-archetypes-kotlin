package org.appsugar.archetypes.web.security


import org.apache.shiro.cache.MemoryConstrainedCacheManager
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor
import org.apache.shiro.spring.web.ShiroFilterFactoryBean
import org.apache.shiro.web.mgt.DefaultWebSecurityManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class SecurityConfiguration{

    @Bean
    fun shiroFilterFactoryBean() = with(ShiroFilterFactoryBean()){
        loginUrl="/login"
        successUrl="/index"
        securityManager = securityManager()
        filterChainDefinitionMap = mapOf("/login" to "authc","/logout" to "logout","/static/**" to "anon","/favicon.ico" to "anon","/**" to "user")
        this
    }


    @Bean
    fun securityManager() = with(DefaultWebSecurityManager()){
        realms = listOf(realm())
        cacheManager = cacheManager()
        this
    }

    @Bean
    fun realm()= ShiroRealm()

    @Bean
    fun cacheManager() = MemoryConstrainedCacheManager()

    @Bean
    fun authorizer() = AuthorizationAttributeSourceAdvisor()


}