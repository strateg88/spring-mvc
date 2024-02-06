package com.muratov.springmvc.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import com.muratov.springmvc.dto.Entity
import com.muratov.springmvc.service.AddressBookService

@RestController
@RequestMapping("/api")
class RestController @Autowired constructor(private val addressBookService: AddressBookService) {
    @PostMapping("/add")
    fun add(@RequestBody entity: Entity): ResponseEntity<Nothing> {
        addressBookService.add(entity)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @GetMapping("/list")
    fun list(@RequestParam(required = false) query: String?): ResponseEntity<Set<Map.Entry<Long, Entity>>> {
        return ResponseEntity.ok(addressBookService.list(query))
    }

    @GetMapping("{entityId}/view")
    fun view(@PathVariable entityId: Long): ResponseEntity<Entity> {
        return ResponseEntity.ok().body(
            addressBookService.view(entityId) ?: return ResponseEntity.notFound().build()
        )
    }

    @PutMapping("{entityId}/edit")
    fun edit(@PathVariable entityId: Long, @RequestBody entry: Entity): ResponseEntity<Nothing> {
        addressBookService.edit(entityId, entry) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.status(HttpStatus.ACCEPTED).build()
    }

    @DeleteMapping("/{entityId}/delete")
    fun delete(@PathVariable entityId: Long): ResponseEntity<Nothing> {
        addressBookService.delete(entityId) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.status(HttpStatus.ACCEPTED).build()
    }
}