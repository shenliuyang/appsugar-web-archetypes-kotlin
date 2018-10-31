package org.appsugar.archetypes.web


import com.fasterxml.jackson.databind.ObjectMapper
import org.appsugar.archetypes.common.domain.Response
import org.appsugar.archetypes.service.UserDetailServiceImpl
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher


@Configuration
class WebConfiguration : WebSecurityConfigurerAdapter() {


    override fun configure(http: HttpSecurity) {
        val om = ObjectMapper()
        val unAuthentication = om.writeValueAsBytes(Response.UN_AUTHENTICATED)
        val accessDenine = om.writeValueAsBytes(Response.UN_AUTHROIZED)
        val success = om.writeValueAsBytes(Response.SUCCESS)
        val contentType = "application/json; charset=utf-8"
        http.authorizeRequests()
                .antMatchers("/login").permitAll()
                .anyRequest().fullyAuthenticated()
                .and().logout().logoutRequestMatcher(AntPathRequestMatcher("/logout")).logoutSuccessHandler { _, response, _ ->
                    response.contentType = contentType
                    response.outputStream.write(success)
                }.and().exceptionHandling().authenticationEntryPoint { _, response, _ ->
                    response.contentType = contentType
                    response.outputStream.write(unAuthentication)
                }.accessDeniedHandler { _, response, _ ->
                    response.contentType = contentType
                    response.outputStream.write(accessDenine)
                }
                .and().csrf().disable()
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    override fun configure(auth: AuthenticationManagerBuilder?) {
        super.configure(auth)
        //TODO custom  authentication provider  auth.authenticationProvider(authProvider);
    }
}

@Configuration
@Order(1)
class ActuatorSecurity : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.requestMatcher(EndpointRequest.toAnyEndpoint()).authorizeRequests()
                .anyRequest().hasAuthority(UserDetailServiceImpl.endpointPermission)
                .and().httpBasic()
    }

}