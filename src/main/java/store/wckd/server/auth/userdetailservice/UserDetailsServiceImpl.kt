package store.wckd.server.auth.userdetailservice

import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import store.wckd.server.service.UserService

@Service
class UserDetailsServiceImpl(private val userService: UserService) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userService
                .findByUsername(username)
                .block() ?: throw UsernameNotFoundException("The system could not find a user with username $username")

        return User(
                user.username,
                user.password,
                emptySet()
        )
    }

}