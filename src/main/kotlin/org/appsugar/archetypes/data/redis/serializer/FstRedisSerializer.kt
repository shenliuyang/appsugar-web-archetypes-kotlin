package org.appsugar.archetypes.data.redis.serializer

import org.nustaq.serialization.FSTConfiguration
import org.springframework.data.redis.serializer.RedisSerializer

/**
 * 使用fst代替jdk序列化提升性能减少存储占用
 */
class FstRedisSerializer(private val fstSerializerSource: FstSerializerSource) : RedisSerializer<Any> {


    override fun serialize(t: Any?) = t?.let { fstSerializerSource.fstConfiguration.asByteArray(this) }

    override fun deserialize(bytes: ByteArray?) = bytes?.let { fstSerializerSource.fstConfiguration.asObject(it) }

}

data class FstSerializerSource(val shareReferenfces: Boolean = false, val preRegistryClasses: List<Pair<Int, Class<*>>> = emptyList()) {
    private val threadLocal = object : ThreadLocal<FSTConfiguration>() {
        override fun initialValue(): FSTConfiguration {
            val fst = FSTConfiguration.createDefaultConfiguration()
            fst.registerClass()
            fst.isShareReferences = shareReferenfces
            val registry = fst.classRegistry
            preRegistryClasses.forEach {
                val id = it.first
                val clazz = it.second
                registry.getClazzFromId(id).apply { require(this == null) { "Prepare to registry class $clazz to id $id  but current id already registry by $this" } }
                registry.getIdFromClazz(clazz).apply { require(this == Int.MIN_VALUE) { "Prepare to registry class $clazz to id $id but current class already registry to $this" } }
                registry.registerClass(clazz, id, fst)
            }
            return fst
        }
    }

    val fstConfiguration: FSTConfiguration get() = threadLocal.get()!!
}