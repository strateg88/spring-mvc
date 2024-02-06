package com.muratov.springmvc.service

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import com.muratov.springmvc.dto.User

@Service
class AuthService {
    private var database: ConcurrentHashMap<String, String> = ConcurrentHashMap()

    init {
        database["test"] = "test"
    }

    fun verify(user: User): Boolean {
        return database.get(user.login).equals(user.password) ?: false
    }
}