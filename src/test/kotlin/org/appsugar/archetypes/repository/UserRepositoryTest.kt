package org.appsugar.archetypes.repository

import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.appsugar.archetypes.BaseTestCase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UserRepositoryTest : BaseTestCase() {
    @Autowired
    lateinit var repository: UserRepository


    @Test
    fun testFindByLoginName() = runBlocking {
        val loginName = "admin"
        val user = repository.findByLoginName(loginName).await()
        logger.debug("find user by loginName $loginName  result is $user roles is ${user?.roles}  ${repository.findById(-1)}")
        Assertions.assertNotNull(user)
    }

    @Test
    fun testFindByCondition() {
        val condition = UserCondition(name = "NewYoung", loginName = "123")
        val users = repository.findAll(repository.toPredicate(condition))
        logger.debug("testFindByCondition {} result is {} ", condition, users)
    }
}