package org.appsugar.archetypes.web.controller

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.ModelAttribute


open class BaseController {
    @ModelAttribute
    fun pageable(@PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable) = pageable
}