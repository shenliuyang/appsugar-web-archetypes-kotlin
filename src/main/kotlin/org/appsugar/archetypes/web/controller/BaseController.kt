package org.appsugar.archetypes.web.controller

import org.appsugar.archetypes.repository.jpa.BaseJpaRepository
import org.appsugar.archetypes.util.blockedMono
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.ModelAttribute
import reactor.core.publisher.Mono

abstract class BaseController<T> {
    protected val logger = LoggerFactory.getLogger(this::class.java)!!

    @Autowired
    open lateinit var jpaRepository: BaseJpaRepository<T, Long>

    @ModelAttribute("entity")
    open fun entity(id: IdData): Mono<T?> = blockedMono { if (id.id == null) null else jpaRepository.findById(id.id!!).orElse(null) }

    @ModelAttribute
    open fun pageable(pageData: PageData) = PageRequest.of(pageData.page, pageData.size, Sort.Direction.DESC, "id")

    inline fun <T, R> Page<T>.transfer(block: (T) -> R): Page<R> {
        val content = content.map(block)
        return PageImpl<R>(content, pageable, totalElements)
    }
}

data class PageData(var page: Int = 0, var size: Int = 25)

data class IdData(var id: Long?)