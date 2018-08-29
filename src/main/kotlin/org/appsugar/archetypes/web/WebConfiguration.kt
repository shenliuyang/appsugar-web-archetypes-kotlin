package org.appsugar.archetypes.web


import com.fasterxml.jackson.databind.ObjectMapper
import com.hazelcast.config.Config
import com.hazelcast.config.XmlConfigBuilder
import org.appsugar.archetypes.common.domain.Response
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.AbstractEnvironment
import org.springframework.core.env.EnumerablePropertySource
import org.springframework.core.env.Environment
import org.springframework.core.env.PropertySource
import org.springframework.core.io.Resource
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import java.util.*
import java.util.stream.StreamSupport


@Configuration
class WebConfiguration : WebSecurityConfigurerAdapter() {


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


    override fun configure(http: HttpSecurity) {
        val om = ObjectMapper()
        val unAuthentication = om.writeValueAsBytes(Response.UN_AUTHENTICATED)
        val accessDenine = om.writeValueAsBytes(Response.UN_AUTHROIZED)
        val loginFailure = om.writeValueAsBytes(Response("用户名或账号密码错误"))
        val success = om.writeValueAsBytes(Response.SUCCESS)
        http.authorizeRequests()
                .antMatchers("/login").permitAll()
                .anyRequest().fullyAuthenticated()
                .and().formLogin().failureHandler { _, response, _ ->
                    response.outputStream.write(loginFailure)
                }.successHandler { _, response, _ ->
                    response.outputStream.write(success)
                }
                .and().exceptionHandling().authenticationEntryPoint { _, response, _ ->
                    response.outputStream.write(unAuthentication)
                }.accessDeniedHandler { _, response, _ ->
                    response.outputStream.write(accessDenine)
                }
                .and().logout().logoutRequestMatcher(AntPathRequestMatcher("/logout")).logoutSuccessHandler { _, response, _ ->
                    response.outputStream.write(success)
                }
                .and().csrf().disable()
    }
}