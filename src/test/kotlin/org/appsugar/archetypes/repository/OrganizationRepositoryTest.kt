package org.appsugar.archetypes.repository

import org.appsugar.archetypes.BaseTestCase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class OrganizationRepositoryTest : BaseTestCase() {

    @Autowired
    lateinit var organizationRepository: OrganizationRepository

    @Test
    fun testFindMaxCodeByCodeLength() {
        val codeLength = 2
        val code = organizationRepository.findMaxCodeByCodeLength(codeLength = codeLength)
        Assertions.assertTrue(code.isPresent)
    }
}