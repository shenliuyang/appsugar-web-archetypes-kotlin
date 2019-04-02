package org.appsugar.archetypes.controller

import kotlinx.coroutines.runBlocking
import org.appsugar.archetypes.BaseControllerTestCase
import org.appsugar.archetypes.TypedPage
import org.appsugar.archetypes.TypedResponse
import org.appsugar.archetypes.await
import org.appsugar.archetypes.common.domain.Response
import org.appsugar.archetypes.entity.User
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

class UserControllerTest : BaseControllerTestCase() {
    lateinit var userFacade: UserFacade
    @Test
    fun testList() = runBlocking {
        val response = userFacade.list().await()!!
        logger.debug("testList result is {}", response)
        Assertions.assertEquals(Response.SUCCESS.code, response.code)
    }

    override fun postConstruct() {
        super.postConstruct()
        userFacade = buildFacade(UserFacade::class.java)
    }
}

interface UserFacade {
    @GET("/system/user/list")
    fun list(): Call<TypedResponse<TypedPage<User>>>
}