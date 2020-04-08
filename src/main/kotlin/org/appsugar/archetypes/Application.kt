package org.appsugar.archetypes


import org.appsugar.archetypes.repository.jpa.BaseJpaRepository
import org.appsugar.archetypes.repository.jpa.CustomSimpleJpaRepository
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.FilterType
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableAsync


@SpringBootApplication
/**
 * special for data jpa repository
 */
@EnableJpaRepositories(repositoryBaseClass = CustomSimpleJpaRepository::class, includeFilters = [ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = [BaseJpaRepository::class])])
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableCaching(proxyTargetClass = true)
@EnableAsync(proxyTargetClass = true)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}


