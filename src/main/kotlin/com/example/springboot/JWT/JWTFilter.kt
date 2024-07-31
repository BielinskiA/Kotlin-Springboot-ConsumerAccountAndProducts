package com.example.springboot.JWT

import com.example.springboot.Managers.AdminManager
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*
import org.springframework.security.core.userdetails.User

@Component

class HeaderAuthenticationJwtFilter: OncePerRequestFilter() {
    private val loggers = LoggerFactory.getLogger(HeaderAuthenticationJwtFilter::class.java)
    private val jwtManager = JWTController()

    companion object{
        const val USER = "USER"
        const val ERROR = "ERROR"
    }
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val user = AdminManager()
            val authHeader = request.getHeader("Authorization")?.trim()
            if (authHeader.isNullOrBlank()) {
                request.setAttribute(ERROR, "Brak uprawnień")
                filterChain.doFilter(request, response)
                return
            }
            if (jwtManager.verifyJwt(authHeader)) {
                val userId = jwtManager.verifyJwtById(authHeader)
                val userAccount = user.getAdminById(userId).firstOrNull()
                    ?: return filterChain.doFilter(request, response)
                val hash = AdminManager().hash(userAccount.password)
                val authUser = User(
                    /* username = */ userAccount.name,
                    /* password = */ hash,
                    /* enabled = */ true,
                    /* accountNonExpired = */ true,
                    /* credentialsNonExpired = */ true,
                    /* accountNonLocked = */ true,
                    /* authorities = */ Collections.singletonList(SimpleGrantedAuthority("ROLE_USER"))
                )
                val authentication = UsernamePasswordAuthenticationToken(
                    /* principal = */authUser,
                    /* credentials = */null,
                    /* authorities = */authUser.authorities
                )

                SecurityContextHolder.getContext().authentication = authentication;
                request.setAttribute(USER, userAccount)

            }

            filterChain.doFilter(request, response)

        } catch (e: Exception) {
            loggers.error(e.stackTraceToString())
            filterChain.doFilter(request, response)
        }
    }
}