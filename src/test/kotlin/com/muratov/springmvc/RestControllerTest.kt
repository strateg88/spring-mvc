package com.muratov.springmvc

import org.junit.jupiter.api.Assertions.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.*
import com.muratov.springmvc.dto.Entity
import com.muratov.springmvc.service.AddressBookService
import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class RestControllerTest {
    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var addressBookService: AddressBookService
    private lateinit var testEntity01: Entity
    private lateinit var testEntity02: Entity
    private fun url(path: String) = "http://localhost:${port}/${path}"

    @BeforeEach
    fun setUp() {
        testEntity01 = Entity(addressBookService.getNextEntityId(), "FN_One", "FA_One", "89111234567")
        addressBookService.add(testEntity01)
        testEntity02 = Entity(addressBookService.getNextEntityId(), "FN_Two", "FA_Two", "89211234567")
    }

    @AfterEach
    fun tearDown() = addressBookService.deleteAll()

    @Test
    fun add() {
        val sizeBefore = addressBookService.size()
        val rs = restTemplate.exchange(
            url("/api/add"),
            HttpMethod.POST,
            HttpEntity(testEntity02, getAuthHeader()),
            Nothing::class.java
        )
        val sizeAfter = addressBookService.size()
        assertEquals(HttpStatus.CREATED, rs.statusCode)
        assertEquals(1, sizeAfter - sizeBefore)
    }

    @Test
    fun view() {
        val rs = restTemplate.exchange(
            url("/api/${testEntity01.entityId}/view"),
            HttpMethod.GET,
            HttpEntity<Nothing>(getAuthHeader()),
            Entity::class.java
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(rs.body?.entityId).isEqualTo(testEntity01.entityId)
        assertThat(rs.body?.fullName).isEqualTo(testEntity01.fullName)
        assertThat(rs.body?.fullAddress).isEqualTo(testEntity01.fullAddress)
        assertThat(rs.body?.phoneNumber).isEqualTo(testEntity01.phoneNumber)
    }

    @Test
    fun edit() {
        val rs = restTemplate.exchange(
            url("/api/${testEntity01.entityId}/edit"),
            HttpMethod.PUT,
            HttpEntity(testEntity02, getAuthHeader()),
            Nothing::class.java
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.ACCEPTED)
    }

    @Test
    fun list() {
        val rs = restTemplate.exchange(
            url("/api/list"),
            HttpMethod.GET,
            HttpEntity<Nothing>(getAuthHeader()),
            List::class.java
        )
        assertThat(rs.statusCode).isEqualTo(HttpStatus.OK)
        assertTrue(rs.body?.get(0).toString().contains(testEntity01.fullName!!))
        assertTrue(rs.body?.get(0).toString().contains(testEntity01.fullAddress!!))
        assertTrue(rs.body?.get(0).toString().contains(testEntity01.phoneNumber!!))
    }

    @Test
    fun delete() {
        val sizeBefore = addressBookService.size()
        val rs = restTemplate.exchange(
            url("/api/${testEntity01.entityId}/delete"),
            HttpMethod.DELETE,
            HttpEntity(testEntity01.entityId, getAuthHeader()),
            Nothing::class.java
        )
        val sizeAfter = addressBookService.size()
        assertThat(rs.statusCode).isEqualTo(HttpStatus.ACCEPTED)
        assertEquals(1, sizeBefore - sizeAfter)
    }

    private fun getAuthHeader(): HttpHeaders =
        HttpHeaders().also { it.add("Cookie", "auth=${LocalDateTime.now().plusMinutes(5)}") }
}