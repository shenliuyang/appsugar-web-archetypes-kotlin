package org.appsugar.archetypes.repository

import kotlinx.coroutines.experimental.async
import org.appsugar.archetypes.BaseTestCase
import org.appsugar.archetypes.condition.UserCondition
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UserRepositoryTest : BaseTestCase() {
    @Autowired
    lateinit var repository: UserRepository
    

    @Test
    fun testFindByLoginName() {
        val loginName = "admin"
        val user = repository.findByLoginName(loginName)
        logger.debug("find user by loginName $loginName  result is $user roles is ${user?.roles}  ${repository.findById(-1)}")
        Assertions.assertNotNull(user)
    }

    @Test
    fun testFindByCondition() {
        val condition = UserCondition(name = "NewYoung", loginName = "123")
        val users = repository.findAll(UserSpecification(condition))
        logger.debug("testFindByCondition {} result is {} ", condition, users)
        async { }
    }
}