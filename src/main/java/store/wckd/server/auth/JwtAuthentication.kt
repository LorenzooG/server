package store.wckd.server.auth

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import store.wckd.server.entity.User

data class JwtAuthentication(
        /** current user session */
        private val principal: User? = null,

        /** jwt token */
        private val credentials: String = "",
        private val name: String = "JwtAuthentication",
        private val authorities: Collection<GrantedAuthority> = emptySet(),
        private val isAuthenticated: Boolean = false
) : Authentication {
    override fun getAuthorities() = authorities
    override fun getName() = name
    override fun getPrincipal() = principal
    override fun getDetails() = error("Details in a JwtAuthentication will never exists, so do not try to get it.")
    override fun getCredentials() = credentials

    override fun isAuthenticated() = isAuthenticated

    @Deprecated(
            message = "Copy the object to make any changes to make easier to manage the changes",
            replaceWith = ReplaceWith("jwtAuthentication.copy(isAuthenticated = true)")
    )
    override fun setAuthenticated(isAuthenticated: Boolean) = Unit
}