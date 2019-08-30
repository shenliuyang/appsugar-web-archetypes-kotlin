package org.appsugar.archetypes.data.redis

import org.appsugar.archetypes.data.redis.serializer.FstSerializerSource
import org.appsugar.archetypes.util.getLogger
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.annotation.AnnotationAttributes
import org.springframework.core.type.AnnotationMetadata
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.util.ClassUtils
import java.lang.annotation.Inherited
import kotlin.reflect.KClass


class FstClassMappingRegistry : ImportBeanDefinitionRegistrar {
    companion object {
        val logger = getLogger<FstClassMappingRegistry>()
    }

    override fun registerBeanDefinitions(importingClassMetadata: AnnotationMetadata, registry: BeanDefinitionRegistry) {
        val configs = getEnableFstClassMappingAttributes(importingClassMetadata)
        require(configs.isNotEmpty()) { "Annotation EnableFstClassMappings on ${importingClassMetadata.className} must contain at least one EnableFstClassMapping config" }
        val scanner = ClassPathScanningCandidateComponentProvider(false).apply {
            this.addIncludeFilter(AnnotationTypeFilter(ClassMapping::class.java))
        }
        configs.forEach { attr ->
            val fstConfigurationSource = FstClassMappingConfigurationSource(attr, importingClassMetadata.className)
            val basePackages = fstConfigurationSource.basePackageNames
            val idToClassList = scanAndGetIdToClassList(basePackages, scanner)
            val duplicatedListKey = idToClassList.asSequence().map { it.first }.groupingBy { it }.eachCount().asSequence().filter { it.value > 1 }.map { it.key }.toList()
            require(duplicatedListKey.isEmpty()) { "ClassMapping value duplicated $duplicatedListKey  in $idToClassList" }
            if (logger.isDebugEnabled) {
                logger.debug("scan basepackages is {} class mapping data is {}", basePackages, idToClassList)
            }
            val beanDefinition = GenericBeanDefinition().apply {
                beanClass = FstSerializerSource::class.java
                constructorArgumentValues.apply {
                    val shareReferenfces = fstConfigurationSource.shareReferenfces
                    addGenericArgumentValue(shareReferenfces)
                    addGenericArgumentValue(idToClassList)
                }
            }
            val name = fstConfigurationSource.name.let { if (it.isBlank()) "fstSerializerSource" else it }
            registry.registerBeanDefinition(name, beanDefinition)
        }
    }

    /**
     * 支持多个fstClassMapping映射
     */
    fun getEnableFstClassMappingAttributes(importingClassMetadata: AnnotationMetadata) = mutableListOf<AnnotationAttributes>().apply {
        importingClassMetadata.getAnnotationAttributes(EnableFstClassMapping::class.java.name)?.apply { add(AnnotationAttributes(this)) }
        importingClassMetadata.getAnnotationAttributes(EnableFstClassMappings::class.java.name)?.apply {
            @Suppress("UNCHECKED_CAST")
            val ats = this["value"] as (Array<AnnotationAttributes>)
            addAll(ats)
        }
    }

    /**
     * 扫描并获取id对应关系
     */
    fun scanAndGetIdToClassList(packages: Set<String>, scanner: ClassPathScanningCandidateComponentProvider) = mutableListOf<Pair<Int, Class<*>>>().apply {
        val idToClassList = packages.asSequence().flatMap { scanner.findCandidateComponents(it).asSequence() }.distinct().map { it.beanClassName }.map(::classNameToPair).toList()
        addAll(idToClassList)
    }


    fun classNameToPair(className: String): Pair<Int, Class<*>> {
        val clazz = Thread.currentThread().contextClassLoader.loadClass(className)
        val annotation = clazz.getAnnotation(ClassMapping::class.java)
        return annotation.value to clazz
    }

}

class FstClassMappingConfigurationSource(private val attr: AnnotationAttributes, private val metadataClassName: String) {

    val name get() = attr.getString(EnableFstClassMapping::name.name)

    val shareReferenfces get() = attr.getBoolean(EnableFstClassMapping::shareReferenfces.name)

    val basePackageNames
        get() = mutableSetOf<String>().apply {
            val value = attr.getStringArray(EnableFstClassMapping::value.name)
            val basePackages = attr.getStringArray(EnableFstClassMapping::basePackages.name)
            val basePackageClasses = attr.getClassArray(EnableFstClassMapping::basePackageClasses.name)
            // Default configuration - return package of annotated class
            if (value.isEmpty() && basePackages.isEmpty() && basePackageClasses.isEmpty()) {
                val className = metadataClassName
                add(ClassUtils.getPackageName(className))
                return@apply
            }
            addAll(value)
            addAll(basePackages)
            addAll(basePackageClasses.map { it.`package`.name })
        }
}


/**
 * 开启对象id映射自动收集器
 * @param value  Alias for the {@link #basePackages()}
 * @param basePackages scanning basePackages found annotated with ClassMapping and auto registry
 *
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@Import(FstClassMappingRegistry::class)
annotation class EnableFstClassMapping(val value: Array<String> = [], val basePackages: Array<String> = [], val basePackageClasses: Array<KClass<*>> = [], val shareReferenfces: Boolean = false, val name: String = "")

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@Import(FstClassMappingRegistry::class)
annotation class EnableFstClassMappings(val value: Array<EnableFstClassMapping>)

/**
 * 指定class对应的id
 * 缩减序列化后的体积
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ClassMapping(val value: Int)