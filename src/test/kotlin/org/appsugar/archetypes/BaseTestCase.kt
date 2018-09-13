package org.appsugar.archetypes

import org.appsugar.archetypes.common.domain.Response
import org.dbunit.ant.Operation
import org.dbunit.database.DatabaseConfig
import org.dbunit.database.DatabaseConnection
import org.dbunit.database.DefaultMetadataHandler
import org.dbunit.database.ForwardOnlyResultSetTableFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.LinkedMultiValueMap
import java.io.File
import java.sql.DriverManager
import javax.annotation.PostConstruct

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
abstract class BaseTestCase {
    protected val logger: Logger by lazy { LoggerFactory.getLogger(this::class.java) }
    @Autowired
    protected lateinit var restTemplate: TestRestTemplate
    @Autowired
    protected lateinit var env: Environment

    companion object {
        private var flag = false
        private var loginFlag = false
    }

    @PostConstruct
    fun postConstruct() {
        prepareImportSampleData()
        prepareLogin()
    }

    fun prepareLogin() {
        if (BaseTestCase.loginFlag) return
        BaseTestCase.loginFlag = true
        val username = "admin"
        val password = "admin"
        logger.debug("prepare to login with user {} password is {}", username, password)
        val responseEntity = restTemplate.postForEntity("/login", LinkedMultiValueMap<String, String>(mapOf("username" to listOf(username), "password" to listOf(password))), Response::class.java)
        val response = responseEntity.body!!
        logger.debug("login  result is {}", response)
        Assertions.assertEquals(Response.SUCCESS.code, response.code, "login error with $username  and password $password")
        val loginHeader = responseEntity.headers
        restTemplate.restTemplate.interceptors = listOf(ClientHttpRequestInterceptor { request, body, execution ->
            loginHeader["Set-Cookie"]?.let {
                request.headers["Cookie"] = it
            }
            return@ClientHttpRequestInterceptor execution.execute(request, body)
        })
    }


    fun prepareImportSampleData() {
        env.getProperty("refreshDb") ?: return
        if (flag) return else flag = true
        logger.info("prepare to import test db data")
        val con = DriverManager.getConnection(env["spring.datasource.url"], env["spring.datasource.username"], env["spring.datasource.password"])
        val connection = DatabaseConnection(con)
        val config = connection.config
        config.setProperty(DatabaseConfig.PROPERTY_RESULTSET_TABLE_FACTORY, ForwardOnlyResultSetTableFactory())
        config.setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false)
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, Class.forName(env["spring.dbunit.data-factory"]).newInstance())
        config.setProperty(DatabaseConfig.PROPERTY_METADATA_HANDLER, DefaultMetadataHandler())
        var operation = with(Operation()) {
            isTransaction = true
            type = env["spring.dbunit.operationType"]
            format = "flat"
            src = File(env["spring.dbunit.sample-file"])
            this
        }
        try {
            operation.execute(connection)
        } finally {
            connection.close()
        }
    }
}


