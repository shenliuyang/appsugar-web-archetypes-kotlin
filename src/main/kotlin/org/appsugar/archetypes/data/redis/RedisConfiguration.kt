package org.appsugar.archetypes.data.redis

import org.appsugar.archetypes.Application
import org.appsugar.archetypes.data.redis.serializer.FstRedisSerializer
import org.appsugar.archetypes.util.getLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.context.annotation.Configuration
import org.springframework.core.type.filter.AnnotationTypeFilter


@Configuration
class RedisConfiguration {
    companion object {
        val logger = getLogger<RedisConfiguration>()
    }

    @Bean
    fun fstRedisSerializer(): FstRedisSerializer {
        val scanner = ClassPathScanningCandidateComponentProvider(false).apply {
            this.addIncludeFilter(AnnotationTypeFilter(ClassMapping::class.java))
        }
        val packageName = Application::class.java.`package`.name
        val result = scanner.findCandidateComponents(packageName)
        val classToIdList = result.asSequence().map { it.beanClassName }.map(::classNameToPair).toList()
        logger.debug("prepare to register class mapping {}", classToIdList)
        return FstRedisSerializer(preRegistryClasses = classToIdList)
    }

    fun classNameToPair(className: String): Pair<Int, Class<*>> {
        val clazz = Thread.currentThread().contextClassLoader.loadClass(className)
        val annotation = clazz.getAnnotation(ClassMapping::class.java)
        return annotation.value to clazz
    }

}

/**
 * 指定class对应的id
 * 缩减序列化后的体积
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ClassMapping(val value: Int)