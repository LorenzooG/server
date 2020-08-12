package store.wckd.server.auth

import kotlinx.coroutines.reactor.mono
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import store.wckd.server.service.JwtService

@Component
class JwtAuthenticationManager(private val jwtService: JwtService) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication) = mono<Authentication> {
        if (authentication !is JwtAuthentication)
            error("The authentication needs to be a JwtAuthentication to " +
                    "be handled by the application's AuthenticationManager")

        authentication.copy(
                principal = jwtService.decodeJwtToUser(authentication.credentials),
                isAuthenticated = true
        )
    }
}