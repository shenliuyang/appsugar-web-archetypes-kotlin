package org.appsugar.archetypes.web.security


import org.apache.shiro.cache.MemoryConstrainedCacheManager
import org.apache.shiro.codec.Base64
import org.apache.shiro.mgt.SecurityManager
import org.apache.shiro.session.mgt.SessionManager
import org.apache.shiro.spring.LifecycleBeanPostProcessor
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor
import org.apache.shiro.spring.web.ShiroFilterFactoryBean
import org.apache.shiro.web.mgt.CookieRememberMeManager
import org.apache.shiro.web.mgt.DefaultWebSecurityManager
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class SecurityConfiguration{

     companion object {
        @JvmStatic
        @Bean
        fun lifecycleBeanPostProcessor()= LifecycleBeanPostProcessor()
    }

    @Bean
    fun shiroFilter(securityManager:SecurityManager) = ShiroFilterFactoryBean().let {
            it.securityManager=securityManager
            it.filterChainDefinitionMap=filterChainDefinitionMap()
            it.loginUrl="/login"
            it.successUrl="/index"
            it
    }

    fun filterChainDefinitionMap()= mapOf("/login" to "authc","/logout" to "logout","/static/**" to "anon","/**" to "user")

    @Bean
    fun shiroAdvisor(sm:SecurityManager) = AuthorizationAttributeSourceAdvisor().let { it.securityManager= sm;  it}

    @Bean
    fun securityManager(realm: ShiroRealm, sessionManager:SessionManager):DefaultWebSecurityManager{
        val stm= DefaultWebSecurityManager(realm)
        stm.cacheManager=MemoryConstrainedCacheManager()
        val crmm = CookieRememberMeManager()
        crmm.cipherKey= Base64.decode("aGVsbG8sd29ybGQ=")
        stm.rememberMeManager=crmm
        stm.sessionManager=sessionManager
        return stm
    }

    @Bean
    fun sessionManager()= DefaultWebSessionManager()

    @Bean
    fun shiroRealm()= ShiroRealm()


}