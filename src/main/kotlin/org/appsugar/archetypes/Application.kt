package org.appsugar.archetypes

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.SpringApplication
import org.slf4j.LoggerFactory
import org.appsugar.archetypes.entity.User

@SpringBootApplication
class  Application{
}
fun main(args: Array<String>) {
	SpringApplication.run(Application::class.java, *args)
}