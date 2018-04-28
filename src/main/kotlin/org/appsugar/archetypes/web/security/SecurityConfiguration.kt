package org.appsugar.archetypes.web.security


import com.hazelcast.config.Config
import com.hazelcast.config.XmlConfigBuilder
import com.hazelcast.core.HazelcastInstance
import org.apache.shiro.SecurityUtils
import org.apache.shiro.cache.CacheManager
import org.apache.shiro.hazelcast.cache.HazelcastCacheManager
import org.apache.shiro.mgt.SecurityManager
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor
import org.apache.shiro.spring.web.ShiroFilterFactoryBean
import org.apache.shiro.web.mgt.CookieRememberMeManager
import org.apache.shiro.web.mgt.DefaultWebSecurityManager
import org.apache.shiro.web.servlet.SimpleCookie
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.AbstractEnvironment
import org.springframework.core.env.EnumerablePropertySource
import org.springframework.core.env.Environment
import org.springframework.core.env.PropertySource
import org.springframework.core.io.Resource
import java.util.*
import java.util.stream.StreamSupport


@Configuration
class SecurityConfiguration {

    @Bean
    fun shiroFilterFactoryBean(securityManager: SecurityManager) = ShiroFilterFactoryBean().apply {
        loginUrl = "/login"
        successUrl = "/index"
        this.securityManager = securityManager
        val staticDefinitionMap = mapOf("/css" to "anon", "/images" to "anon", "/js" to "anon", "/scss" to "anon")
        filterChainDefinitionMap = mapOf("/login" to "authc", "/logout" to "logout"
                , "/wro4j/*" to "anon", "/static/**" to "anon", "/favicon.ico" to "anon", "/webjars/**" to "anon", "/**" to "user") + staticDefinitionMap
    }


    @Bean
    fun securityManager(shiroCacheManager: CacheManager, environment: Environment) = DefaultWebSecurityManager().apply {
        realms = listOf(realm())
        cacheManager = shiroCacheManager
        sessionManager = DefaultWebSessionManager().apply {
            isSessionValidationSchedulerEnabled = false
            sessionDAO = EnterpriseCacheSessionDAO()
            sessionIdCookie = SimpleCookie().apply {
                name = environment.getProperty("shiro.session.name", "sessionid")
            }
            val crmm = CookieRememberMeManager()
            //val key = Base64.getEncoder().encode(AesCipherService().generateNewKey().getEncoded()) generate new key
            crmm.cipherKey = Base64.getDecoder().decode(environment.getProperty("shiro.cipher.key", "u39fskcJooyWj2jA6Vs2lA=="))
            rememberMeManager = crmm

        }
        SecurityUtils.setSecurityManager(this)//make sure always have a securityManager
    }

    @Bean
    fun realm() = ShiroRealm()

    @Bean
    fun shiroCacheManager(instance: HazelcastInstance) = HazelcastCacheManager().apply { hazelcastInstance = instance }

    /**把spring跟hazelcast配置结合**/
    @Bean
    fun hazelcastConfig(springEnv: Environment, @Value("classpath:hazelcast.xml") resource: Resource): Config {
        val props = Properties()
        val propSrcs = (springEnv as AbstractEnvironment).propertySources
        StreamSupport.stream<PropertySource<*>>(propSrcs.spliterator(), false)
                .filter { ps -> ps is EnumerablePropertySource<*> }
                .map { ps -> (ps as EnumerablePropertySource<*>).propertyNames }
                .flatMap(Arrays::stream)
                .forEach { propName -> props.setProperty(propName, springEnv.getProperty(propName)) }

        return XmlConfigBuilder(resource.inputStream).setProperties(props).build()
    }


    @Bean
    fun authorizer() = AuthorizationAttributeSourceAdvisor()

    /**fix 404 not found ,  default proxy is jdk dynamic proxy**/
    @Bean
    fun defaultAdvisorAutoProxyCreator() = DefaultAdvisorAutoProxyCreator().apply { isProxyTargetClass = true }


}