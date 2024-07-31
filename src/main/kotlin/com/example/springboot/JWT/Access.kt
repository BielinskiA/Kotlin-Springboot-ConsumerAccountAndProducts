package com.example.springboot.JWT

import com.example.springboot.JWT.HeaderAuthenticationJwtFilter
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint

class Access: AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authException: AuthenticationException?
    ) {
        response?.contentType = MediaType.APPLICATION_JSON_VALUE
        response?.status = HttpServletResponse.SC_UNAUTHORIZED

        val message = request?.getAttribute(HeaderAuthenticationJwtFilter.ERROR)

        val body: MutableMap<String, Any> = HashMap()
        body["message"] = message ?: "Brak dostępu"

        val mapper = ObjectMapper()
        mapper.writeValue(response?.outputStream, body)
    }
}