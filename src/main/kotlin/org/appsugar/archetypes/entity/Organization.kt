package org.appsugar.archetypes.entity

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import javax.persistence.*

/**
 * 组织架构
 * @name 名称
 * @code 代码 36进制编码不可重复
 * @codeLength 代码长度
 * @description 描述
 * @parent 上级组织
 */
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@Table(name = "organization", indexes = [
    Index(columnList = "codeLength")
])
open class Organization(
        open var name: String = "",
        @get:Column(unique = true)
        open var code: String = "",
        open var codeLength: Int = code.length,
        open var description: String = "") : IdEntity() {

    @get:Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @get:ManyToOne(fetch = FetchType.LAZY)
    @get:JoinColumn(name = "parent_id")
    open var parent: Organization? = null

    override fun toString(): String {
        return "Organization(name='$name', code='$code', codeLength=$codeLength, description='$description')"
    }


}