package org.appsugar.archetypes.service

import org.appsugar.archetypes.entity.Organization
import org.appsugar.archetypes.extension.Number36
import org.appsugar.archetypes.extension.getLogger
import org.appsugar.archetypes.repository.OrganizationRepository
import org.springframework.stereotype.Service

@Service
class OrganizationService(val organizationRepository: OrganizationRepository) {
    companion object {
        val logger = getLogger<OrganizationService>()
    }

    fun save(organization: Organization): Organization {
        //处理新增逻辑
        if (organization.id == 0L) {
            //确定code长度
            val codeLength = organization.parent?.let { it.codeLength + 2 } ?: 2
            val maxCode = organization.parent?.let { organizationRepository.findMaxCodeByParentIdAndCodeLength(it.id, codeLength) }
                    ?: organizationRepository.findMaxCodeByCodeLength(codeLength)
            var code = if (maxCode.isPresent) maxCode.get() else (organization.parent?.let { it.code + "00" } ?: "00")
            code = Number36(code).increase().value
            if (code.length < codeLength) {
                val sb = StringBuilder()
                for (i in code.length until codeLength) sb.append("0")
                code = sb.append(code).toString()
            }
            organization.code = code
            organization.codeLength = codeLength
        }
        return organizationRepository.save(organization)
    }
}