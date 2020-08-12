package store.wckd.server.auth

import kotlinx.coroutines.reactor.mono
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import store.wckd.server.service.JwtService

@Component
class JwtAuthenticationConverter : ServerAuthenticationConverter {
    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val AUTHORIZATION_HEADER_PREFIX = "Bearer "
    }

    override fun convert(exchange: ServerWebExchange) = mono<Authentication> {
        val jwt = exchange.request.headers
                .getFirst(AUTHORIZATION_HEADER)
                .orEmpty()
                .replace(AUTHORIZATION_HEADER_PREFIX, "")

        JwtAuthentication(credentials = jwt)
    }
}