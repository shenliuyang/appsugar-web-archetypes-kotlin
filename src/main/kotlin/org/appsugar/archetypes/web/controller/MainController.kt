package org.appsugar.archetypes.web.controller

import org.appsugar.archetypes.extension.getLogger
import org.appsugar.archetypes.repository.UserRepository
import org.springframework.web.bind.annotation.RestController

@RestController
class MainController(val userRepository: UserRepository) {
    companion object {
        val logger = getLogger<MainController>()
    }
}
