package org.appsugar.archetypes.controller

import org.appsugar.archetypes.BaseTestCase
import org.appsugar.archetypes.common.domain.Response
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class UserControllerTest : BaseTestCase() {

    @Test
    fun testList() {
        val response = restTemplate.getForObject("/system/user/list", Response::class.java)
        logger.debug("testList result is {}", response)
        Assertions.assertEquals(Response.SUCCESS.code, response.code)
    }
}