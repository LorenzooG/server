package store.wckd.server.auth.filter

import com.auth0.jwt.exceptions.JWTDecodeException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import store.wckd.server.service.JwtService
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtFilter(
        private val jwtService: JwtService,

        authenticationManager: AuthenticationManager
) : BasicAuthenticationFilter(authenticationManager) {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        try {
            val jwtToken = request
                    .getHeader(AUTHENTICATION_HEADER)
                    .toString()
                    .replace(AUTHENTICATION_HEADER_PREFIX, "")

            SecurityContextHolder
                    .getContext()
                    .authentication = getAuthenticationFromJwtToken(jwtToken)

        } catch (exception: AuthenticationException) {
            if (isIgnoreFailure) {
                chain.doFilter(request, response)
            } else {
                throw exception
            }
            return
        }
        chain.doFilter(request, response)
    }

    private fun getAuthenticationFromJwtToken(jwtTokenString: String): Authentication = try {
        UsernamePasswordAuthenticationToken(jwtService.decodeJwtToUser(jwtTokenString), "")
    } catch (exception: JWTDecodeException) {
        // create an empty authentication token if has any error
        UsernamePasswordAuthenticationToken("", "")
    }

    companion object {
        const val AUTHENTICATION_HEADER = "Authorization"
        const val AUTHENTICATION_HEADER_PREFIX = "Bearer "
    }

}