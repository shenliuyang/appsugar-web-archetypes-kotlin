package org.appsugar.archetypes.service

import org.appsugar.archetypes.BaseTestCase
import org.appsugar.archetypes.entity.Organization
import org.appsugar.archetypes.repository.OrganizationRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class OrganizationServiceTest : BaseTestCase() {
    @Autowired
    lateinit var organizationService: OrganizationService
    @Autowired
    lateinit var organizationRepository: OrganizationRepository

    @Test
    fun testSave() {
        val og = organizationRepository.getOne(-1L)
        val newOg = Organization("1")
        newOg.parent = og
        organizationService.save(newOg)
    }
}