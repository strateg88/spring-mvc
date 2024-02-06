package com.muratov.springmvc.filter

import javax.servlet.annotation.WebFilter
import org.slf4j.Logger
import org.springframework.core.annotation.Order
import javax.servlet.FilterChain
import javax.servlet.http.HttpFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebFilter(urlPatterns = ["/*"])
@Order(1)
class LoggerFilter(private val logger: Logger) : HttpFilter() {
    override fun doFilter(rq: HttpServletRequest?, rs: HttpServletResponse?, chain: FilterChain?) {
        logger.info("${rq?.method} --> ${rq?.requestURL}")
        chain?.doFilter(rq, rs)
    }
}