package com.muratov.springmvc.controller

import com.muratov.springmvc.service.AddressBookService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import com.muratov.springmvc.dto.Entity


@Controller
@RequestMapping("/app")
class MvcController @Autowired constructor(private val addressBookService: AddressBookService) {
    @GetMapping("/add")
    fun add(@ModelAttribute entity: Entity, model: Model): String {
        model.addAttribute("entity", entity)
        return "add"
    }

    @PostMapping("/add")
    fun add(@ModelAttribute entity: Entity): String {
        addressBookService.add(entity)
        return "redirect:/app/list"
    }

    @GetMapping("/list")
    fun list(@RequestParam(required = false) query: String?, model: Model): String {
        model.addAttribute("entityList", addressBookService.list(query))
        return "list"
    }

    @GetMapping("{entityId}/view")
    fun view(@PathVariable entityId: Long, model: Model): String {
        model.addAttribute("entity", addressBookService.view(entityId))
        return "view"
    }

    @GetMapping("{entityId}/edit")
    fun edit(@PathVariable entityId: Long, model: Model): String {
        model.addAttribute("entity", addressBookService.view(entityId))
        return "edit"
    }

    @PostMapping("{entityId}/edit")
    fun edit(@PathVariable entityId: Long, @ModelAttribute entity: Entity): String {
        addressBookService.edit(entityId, entity)
        return "redirect:/app/list"
    }

    @GetMapping("/{entityId}/delete")
    fun delete(@PathVariable entityId: Long): String {
        addressBookService.delete(entityId)
        return "redirect:/app/list"
    }
}