package org.appsugar.archetypes.web.controller

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.ModelAttribute

abstract class BaseController<T> {
    protected val logger = LoggerFactory.getLogger(this::class.java)
    @Autowired
    open lateinit var jpaRepository: JpaRepository<T, Long>

    @ModelAttribute
    open fun entity(id: Long?) = id?.let { jpaRepository.findByIdOrNull(id) }

    @ModelAttribute
    open fun pageable(@PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable) = pageable as PageRequest
}