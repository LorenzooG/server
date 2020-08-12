package store.wckd.server.auth.provider

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import store.wckd.server.entity.User
import store.wckd.server.service.UserService

class AuthenticationProviderImpl(private val userService: UserService, private val passwordEncoder: PasswordEncoder) : AuthenticationProvider {
    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication {
        val securityContext = SecurityContextHolder.getContext()
        val contextAuthentication = securityContext.authentication

        // will search in the context authentication, if is not null then will use it instead of repeat the same process of check.
        if (contextAuthentication != null) return contextAuthentication

        val credentialsToken = authentication as UsernamePasswordAuthenticationToken
        val principal = credentialsToken.principal

        if (principal is User) return credentialsToken

        val username = principal.toString()

        // find user by credentials' username
        val user = userService.findByUsername(username).block()
                ?: throw UsernameNotFoundException("Could not find a user with username $username")

        val password = credentialsToken.credentials ?: ""

        if (!passwordEncoder.matches(password.toString(), user.password))
            throw BadCredentialsException("Your password/username is incorrect!")

        // set the authentication to the context
        securityContext.authentication = authentication

        return UsernamePasswordAuthenticationToken(user, password)
    }

    override fun supports(authentication: Class<*>) =
            authentication.isAssignableFrom(UsernamePasswordAuthenticationToken::class.java)

}