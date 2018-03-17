package org.appsugar.archetypes.repository

import com.querydsl.core.BooleanBuilder
import org.appsugar.archetypes.condition.OrganizationCondition
import org.appsugar.archetypes.entity.Organization
import org.appsugar.archetypes.entity.QOrganization
import org.appsugar.archetypes.extension.isNotBlankThen
import org.appsugar.archetypes.extension.notZero
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import java.util.*

interface OrganizationRepository : JpaRepository<Organization, Long>, QuerydslPredicateExecutor<Organization> {
    /**
     * 根据代码长度获取对应最大代码
     */
    @Query("select max(code) from Organization where codeLength=:codeLength")
    fun findMaxCodeByCodeLength(codeLength: Int = 2): Optional<String>

    @Query("select max(code) from Organization where parent.id=:parentId and codeLength=:codeLength")
    fun findMaxCodeByParentIdAndCodeLength(parentId: Long, codeLength: Int = 2): Optional<String>

}

private val u = QOrganization.organization
fun OrganizationRepository.toPredicate(c: OrganizationCondition) = BooleanBuilder().apply {
    c.parentId.notZero { and(u.parent.id.eq(this)) }
    c.name.isNotBlankThen { and(u.name.startsWith(this)) }
}