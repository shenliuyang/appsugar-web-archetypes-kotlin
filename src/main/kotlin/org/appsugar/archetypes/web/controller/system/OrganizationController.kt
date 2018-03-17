package org.appsugar.archetypes.web.controller.system

import org.appsugar.archetypes.condition.OrganizationCondition
import org.appsugar.archetypes.entity.Organization
import org.appsugar.archetypes.extension.attr
import org.appsugar.archetypes.extension.getLogger
import org.appsugar.archetypes.extension.notZero
import org.appsugar.archetypes.repository.OrganizationRepository
import org.appsugar.archetypes.repository.toPredicate
import org.appsugar.archetypes.service.OrganizationService
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@RequestMapping("/system/org")
@Controller
class OrganizationController(val organizationRepository: OrganizationRepository, val organizationService: OrganizationService) {
    companion object {
        private val logger = getLogger<OrganizationController>()
    }

    @ModelAttribute("org")
    fun modelAttribute(id: Long?) = when (id) {
        null, 0L -> Organization()
        else -> organizationRepository.findById(id).get()
    }

    @RequestMapping(value = ["list", ""])
    fun list(condition: OrganizationCondition, @PageableDefault(sort = ["code"]) pageable: Pageable, model: Model) = model.attr("page", organizationRepository.findAll(organizationRepository.toPredicate(condition), pageable)).attr("condition", condition).let { "/system/org/list" }


    @RequestMapping("form")
    fun form() = "/system/org/form"

    @RequestMapping("/save")
    fun save(parentId: Long?, org: Organization, ra: RedirectAttributes): String {
        parentId.notZero { org.parent = organizationRepository.getOne(this) }
        val saved = organizationService.save(org)
        logger.info("save before {} and after {} ${org.parent}", org, saved)
        ra.addFlashAttribute("msg", "保存[${saved.name}]成功")
        return "redirect:/system/org/list"
    }
}