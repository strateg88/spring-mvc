package com.muratov.springmvc.filter

import javax.servlet.annotation.WebFilter
import org.springframework.core.annotation.Order
import java.time.LocalDateTime
import javax.servlet.FilterChain
import javax.servlet.http.HttpFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebFilter(urlPatterns = ["/app/*", "/api/*"])
@Order(2)
class AuthFilter : HttpFilter() {
    override fun doFilter(rq: HttpServletRequest?, rs: HttpServletResponse?, chain: FilterChain?) {
        if (hasAuthCookie(rq)) {
            chain?.doFilter(rq, rs)
        } else {
            rs?.sendRedirect("/login")
        }
    }

    private fun hasAuthCookie(rq: HttpServletRequest?): Boolean {
        return rq?.cookies?.isNotEmpty() == true && rq.cookies?.any { cookie ->
            cookie.name.equals("auth") && LocalDateTime.parse(cookie.value).plusMinutes(30).isAfter(LocalDateTime.now())
        }!!
    }
}