package org.appsugar.archetypes.web.controller.system

import org.appsugar.archetypes.condition.OrganizationCondition
import org.appsugar.archetypes.entity.Organization
import org.appsugar.archetypes.extension.attr
import org.appsugar.archetypes.extension.getLogger
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

    @ModelAttribute("organization")
    fun modelAttribute(id: Long?) = when (id) {
        null, 0L -> Organization()
        else -> organizationRepository.getOne(id)
    }

    @RequestMapping(value = ["list", ""])
    fun list(condition: OrganizationCondition, @PageableDefault(sort = ["code"]) pageable: Pageable, model: Model) = model.attr("page", organizationRepository.findAll(organizationRepository.toPredicate(condition), pageable)).attr("condition", condition).let { "/system/org/list" }


    @RequestMapping("form")
    fun form(organization: Organization, model: Model): String {
        return "/system/org/form"
    }

    @RequestMapping("/save")
    fun save(organization: Organization, ra: RedirectAttributes): String {
        if (organization.parent?.id == 0L) {
            organization.parent = null
        }
        val saved = organizationService.save(organization)
        logger.info("save before {} and after {} ${organization.parent}", organization, saved)
        ra.addFlashAttribute("msg", "保存[${saved.name}]成功")
        return "redirect:/system/org/list"
    }
}