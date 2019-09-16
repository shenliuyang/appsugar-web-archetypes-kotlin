package org.appsugar.archetypes.web.controller

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactor.mono
import org.appsugar.archetypes.repository.BaseRepository
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
    open lateinit var jpaRepository: BaseRepository<T, Long>

    @ModelAttribute("entity")
    open fun entity(id: IdData): Mono<T> = if (id.id == null) Mono.empty() else mono(Dispatchers.Unconfined) { jpaRepository.findByIdOrNullAsync(id.id!!).await() }

    @ModelAttribute
    open fun pageable(pageData: PageData) = PageRequest.of(pageData.page, pageData.size, Sort.Direction.DESC, "id")

    inline fun <T, R> Page<T>.transfer(block: (T) -> R): Page<R> {
        val content = content.map(block)
        return PageImpl<R>(content, pageable, totalElements)
    }
}

data class PageData(var page: Int = 0, var size: Int = 25)

data class IdData(var id: Long?)