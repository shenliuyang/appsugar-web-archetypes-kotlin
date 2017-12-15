package org.appsugar.archetypes


import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableWebMvc
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
