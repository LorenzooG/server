package store.wckd.server.auth

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import store.wckd.server.entity.User

/**
 * Class used for session
 *
 * @param principal current session, could be null if the user is not logged,
 *   could be called in a RestController with annotation @AuthenticationPrincipal
 * @param credentials jwt token used/to be used to login
 * @param name session name
 * @param authorities scopes of the session
 * @param isAuthenticated determine if the session is successfully authenticated
 */
data class JwtAuthentication(
        /** current user session */
        private val principal: User? = null,

        /** jwt token */
        private val credentials: String = "",
        private val name: String = "JwtAuthentication",
        private val authorities: Collection<GrantedAuthority> = emptySet(), // TODO: implement session scopes
        private val isAuthenticated: Boolean = false
) : Authentication {
    override fun getAuthorities() = authorities
    override fun getName() = name
    override fun getPrincipal() = principal
    override fun getDetails() = error("Details in a JwtAuthentication will never exists, so do not try to get it.")
    override fun getCredentials() = credentials

    override fun isAuthenticated() = isAuthenticated

    /**
     * Nothing in this session is mutable, clone the session so then change the values:
     *
     * ```kotlin
     * val session: JwtAuthentication = /* your session */
     * val authenticatedSession = session.copy(
     *   isAuthenticated = true
     * )
     * ```
     *
     * The same is supposed to be used when you change another field value.
     */
    @Deprecated(
            message = "Copy the object to make any changes to make easier to manage the changes",
            replaceWith = ReplaceWith("jwtAuthentication.copy(isAuthenticated = true)")
    )
    override fun setAuthenticated(isAuthenticated: Boolean) = Unit
}