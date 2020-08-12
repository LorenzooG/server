package store.wckd.server.auth.session

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.session.SessionAuthenticationException
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy
import store.wckd.server.dto.LoginResponseDTO
import store.wckd.server.entity.User
import store.wckd.server.service.JwtService
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class SessionAuthenticationStrategyImpl(private val jwtService: JwtService) : SessionAuthenticationStrategy {
    @Throws(SessionAuthenticationException::class)
    override fun onAuthentication(
            authentication: Authentication,
            request: HttpServletRequest,
            response: HttpServletResponse
    ) {
        val principal = authentication.principal ?: throw SessionAuthenticationException("Principal is null")
        val user = principal as? User ?: throw SessionAuthenticationException("Principal is not user")

        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"

        val jwtString = jwtService.encodeJwt(user)

        response.writer.use { writer ->
            writer.print(objectMapper.writeValueAsString(LoginResponseDTO(jwtString)))
            writer.flush()
        }
    }

    companion object {
        private val objectMapper = ObjectMapper()
    }

}