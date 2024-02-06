package com.muratov.springmvc.servlet

import java.time.LocalDateTime
import org.slf4j.Logger
import javax.servlet.annotation.WebServlet
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import com.muratov.springmvc.dto.User
import com.muratov.springmvc.service.AuthService

@WebServlet(urlPatterns = ["/login"])
class AuthServlet(private val authService: AuthService, private val logger: Logger) : HttpServlet() {
    override fun doGet(rq: HttpServletRequest?, rs: HttpServletResponse?) {
        servletContext.getRequestDispatcher("/login.html").forward(rq, rs)
    }

    override fun doPost(rq: HttpServletRequest?, rs: HttpServletResponse?) {
        val user = User(rq!!.getParameter("login"), rq.getParameter("password"))
        if (authService.verify(user)) {
            val cookie = Cookie("auth", LocalDateTime.now().toString())
            rs?.addCookie(cookie)
            rs?.sendRedirect("/app/list")
            logger.info("User ${user.login} logged successfully")
        } else {
            rs?.sendRedirect("/login")
            logger.info("Permission denied for user ${user.login}")
        }
    }
}