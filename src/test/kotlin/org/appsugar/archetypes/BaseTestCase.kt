package org.appsugar.archetypes

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.boot.test.context.SpringBootTest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.ActiveProfiles

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class BaseTestCase{
	protected val logger:Logger=LoggerFactory.getLogger(this::class.java)
}