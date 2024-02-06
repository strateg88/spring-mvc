package com.muratov.springmvc

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import com.muratov.springmvc.dto.Entity
import javax.servlet.http.Cookie
import com.muratov.springmvc.service.AddressBookService
import java.time.LocalDateTime
import org.hamcrest.Matchers.not

@SpringBootTest
@AutoConfigureMockMvc
internal class MvcControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var addressBookService: AddressBookService
    private lateinit var testAuthCookie: Cookie
    private lateinit var testEntity01: Entity
    private lateinit var testEntity02: Entity

    @BeforeEach
    fun setUp() {
        testEntity01 = Entity(addressBookService.getNextEntityId(), "FN_One", "FA_One", "89111234567")
        addressBookService.add(testEntity01)
        testEntity02 = Entity(addressBookService.getNextEntityId(), "FN_Two", "FA_Two", "89211234567")
        testAuthCookie = Cookie("auth", LocalDateTime.now().plusMinutes(5).toString())
    }

    @AfterEach
    fun tearDown() = addressBookService.deleteAll()

    @Test
    fun add() {
        mockMvc.perform(
            post("/app/add")
                .flashAttr("entity", testEntity02)
                .cookie(testAuthCookie)
        )
            .andDo(print())
            .andExpect(status().is3xxRedirection)
            .andExpect(MockMvcResultMatchers.view().name("redirect:/app/list"))
        isViewExpected(
            addressBookService.size().toString(),
            testEntity02.fullName!!,
            testEntity02.fullAddress!!,
            testEntity02.phoneNumber!!
        )
    }

    @Test
    fun view() {
        isViewExpected(
            testEntity01.entityId.toString(),
            testEntity01.fullName!!,
            testEntity01.fullAddress!!,
            testEntity01.phoneNumber!!
        )
    }

    @Test
    fun edit() {
        mockMvc.perform(
            post("/app/{entityId}/edit", testEntity01.entityId)
                .flashAttr("entity", testEntity02)
                .cookie(testAuthCookie)
        )
            .andDo(print())
            .andExpect(status().is3xxRedirection)
            .andExpect(MockMvcResultMatchers.view().name("redirect:/app/list"))
        isViewExpected(
            testEntity01.entityId.toString(),
            testEntity02.fullName!!,
            testEntity02.fullAddress!!,
            testEntity02.phoneNumber!!
        )
    }

    @Test
    fun list() {
        mockMvc.perform(
            get("/app/list")
                .cookie(testAuthCookie)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("list"))
            .andExpect(content().string(containsString(testEntity01.fullName)))
            .andExpect(content().string(containsString(testEntity01.fullAddress)))
            .andExpect(content().string(containsString(testEntity01.phoneNumber)))
    }

    @Test
    fun delete() {
        mockMvc.perform(
            get("/app/{entityId}/delete", testEntity01.entityId)
                .cookie(testAuthCookie)
        )
            .andDo(print())
            .andExpect(status().is3xxRedirection)
            .andExpect(MockMvcResultMatchers.view().name("redirect:/app/list"))
        mockMvc.perform(
            get("/app/list")
                .cookie(testAuthCookie)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("list"))
            .andExpect(content().string(not(containsString(testEntity01.fullName))))
            .andExpect(content().string(not(containsString(testEntity01.fullAddress))))
            .andExpect(content().string(not(containsString(testEntity01.phoneNumber))))
    }

    private fun isViewExpected(entityId: String, fullName: String, fullAddress: String, phoneNumber: String) {
        mockMvc.perform(
            get("/app/{entityId}/view", entityId)
                .cookie(testAuthCookie)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("view"))
            .andExpect(content().string(containsString(fullName)))
            .andExpect(content().string(containsString(fullAddress)))
            .andExpect(content().string(containsString(phoneNumber)))
    }
}