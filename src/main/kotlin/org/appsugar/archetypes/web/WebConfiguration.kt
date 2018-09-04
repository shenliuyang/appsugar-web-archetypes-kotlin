package org.appsugar.archetypes.web


import com.fasterxml.jackson.databind.ObjectMapper
import com.hazelcast.config.Config
import com.hazelcast.config.MapAttributeConfig
import com.hazelcast.config.MapIndexConfig
import org.appsugar.archetypes.common.domain.Response
import org.appsugar.archetypes.service.UserDetailServiceJdbcImpl
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.session.hazelcast.HazelcastSessionRepository
import org.springframework.session.hazelcast.PrincipalNameExtractor


@Configuration
class WebConfiguration : WebSecurityConfigurerAdapter() {


    @Bean
    @ConfigurationProperties("spring.hazelcast")
    fun hazelcastConfigProperties() = HazelcastConfig()

    @Bean
    fun hazelcastConfig(c: HazelcastConfig) = Config().apply {
        instanceName = c.name
        groupConfig.apply {
            name = c.group.name
            password = c.group.password
        }
        managementCenterConfig.apply {
            isEnabled = c.management.enabled
            url = c.management.url
        }
        networkConfig.apply {
            interfaces.apply {
                isEnabled = c.network.interfaces.enabled
                interfaces = c.network.interfaces.interfaces
            }
        }
        //config spring session
        val attributeConfig = MapAttributeConfig()
                .setName(HazelcastSessionRepository.PRINCIPAL_NAME_ATTRIBUTE)
                .setExtractor(PrincipalNameExtractor::class.java.name)
        getMapConfig(HazelcastSessionRepository.DEFAULT_SESSION_MAP_NAME)
                .addMapAttributeConfig(attributeConfig)
                .addMapIndexConfig(MapIndexConfig(
                        HazelcastSessionRepository.PRINCIPAL_NAME_ATTRIBUTE, false))

    }


    override fun configure(http: HttpSecurity) {
        val om = ObjectMapper()
        val unAuthentication = om.writeValueAsBytes(Response.UN_AUTHENTICATED)
        val accessDenine = om.writeValueAsBytes(Response.UN_AUTHROIZED)
        val loginFailure = om.writeValueAsBytes(Response(-1, "用户名或账号密码错误"))
        val success = om.writeValueAsBytes(Response.SUCCESS)
        val contentType = "application/json; charset=utf-8"
        http.authorizeRequests()
                .antMatchers("/login").permitAll()
                .anyRequest().fullyAuthenticated()
                .and().formLogin().failureHandler { _, response, _ ->
                    response.contentType = contentType
                    response.outputStream.write(loginFailure)
                }.successHandler { _, response, _ ->
                    response.contentType = contentType
                    response.outputStream.write(success)
                }
                .and().exceptionHandling().authenticationEntryPoint { _, response, _ ->
                    response.contentType = contentType
                    response.outputStream.write(unAuthentication)
                }.accessDeniedHandler { _, response, _ ->
                    response.contentType = contentType
                    response.outputStream.write(accessDenine)
                }
                .and().logout().logoutRequestMatcher(AntPathRequestMatcher("/logout")).logoutSuccessHandler { _, response, _ ->
                    response.contentType = contentType
                    response.outputStream.write(success)
                }
                .and().csrf().disable()
    }
}

@Configuration
@Order(1)
class ActuatorSecurity : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.requestMatcher(EndpointRequest.toAnyEndpoint()).authorizeRequests()
                .anyRequest().hasAuthority(UserDetailServiceJdbcImpl.endpointPermission)
                .and().httpBasic()
    }

}


class HazelcastConfig {
    var group = HazelcastGroupConfig()
    var management = HazelcastManagementConfig()
    var network = HazelcastNetworkConfig()
    var name = ""
    override fun toString(): String {
        return "HazelcastConfig(group=$group, management=$management, network=$network)"
    }
}

class HazelcastGroupConfig {
    var name = ""
    var password = ""
    override fun toString(): String {
        return "HazelcastGroupConfig(name='$name', password='$password')"
    }
}

class HazelcastManagementConfig {
    var enabled = false
    var url = ""
    override fun toString(): String {
        return "HazelcastManagementConfig(enabled=$enabled, url='$url')"
    }
}

class HazelcastNetworkConfig {
    var interfaces = HazelcastInterfacesConfig()
    override fun toString(): String {
        return "HazelcastNetworkConfig(interfaces=$interfaces)"
    }
}

class HazelcastInterfacesConfig {
    var enabled = false
    var interfaces = mutableListOf<String>()
    override fun toString(): String {
        return "HazelcastInterfacesConfig(enabled=$enabled, interfaces=$interfaces)"
    }

}