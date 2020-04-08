package org.appsugar.archetypes.repository

import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.appsugar.archetypes.BaseTestCase
import org.appsugar.archetypes.repository.jpa.RoleJpaRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class RoleRepositoryTest : BaseTestCase() {
    @Autowired
    lateinit var roleRepository: RoleJpaRepository

    @Test
    fun testFindByIdIn() = runBlocking {
        val roles = roleRepository.findByIdIn(listOf(-1L)).await()
        logger.debug("find by id in -1  result is $roles")
        Assertions.assertTrue(roles.isNotEmpty())
    }

}