package store.wckd.server.auth

import kotlinx.coroutines.reactor.mono
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import store.wckd.server.service.JwtService

/**
 * Manage authentication
 */
@Component
class JwtAuthenticationManager(private val jwtService: JwtService) : ReactiveAuthenticationManager {
    /**
     * Try to authorize the session, if can't, will return the old session
     */
    override fun authenticate(authentication: Authentication) = mono<Authentication> {
        if (authentication !is JwtAuthentication)
            error("The authentication needs to be a JwtAuthentication to " +
                    "be handled by the application's AuthenticationManager")

        try {
            authentication.copy(
                    principal = jwtService.decodeJwtToUser(authentication.credentials),
                    isAuthenticated = true
            )
        } catch (exception: Exception) {
            authentication
        }
    }
}