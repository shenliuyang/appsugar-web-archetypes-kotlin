package org.appsugar.archetypes


import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
