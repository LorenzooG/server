package store.wckd.server.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import store.wckd.server.dto.login.LoginRequestDTO
import store.wckd.server.dto.login.LoginResponseDTO
import store.wckd.server.dto.user.UserResponseDTO
import store.wckd.server.entity.User
import store.wckd.server.service.JwtService
import store.wckd.server.service.UserService

/**
 * Controller that controls all of session-related:
 *  - Login [LOGIN_ENDPOINT]
 *  - View current session [SESSION_ENDPOINT], get the current logged user
 *  - Register TODO
 *  - ...
 */
@RestController
class SessionController(
        private val userService: UserService,
        private val jwtService: JwtService,
        private val passwordEncoder: PasswordEncoder
) {
    companion object {
        const val LOGIN_ENDPOINT = "/login"
        const val SESSION_ENDPOINT = "/session"
    }

    @PostMapping(LOGIN_ENDPOINT)
    suspend fun login(@RequestBody body: LoginRequestDTO): ResponseEntity<*> {
        val user: User = userService.findByUsername(body.username)

        if(!passwordEncoder.matches(body.password, user.password))
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(mapOf(
                            "message" to "Invalid password or username."
                    ))

        return ResponseEntity.ok(LoginResponseDTO(jwtService.encodeJwt(user)))
    }

    @GetMapping(SESSION_ENDPOINT)
    suspend fun session(@AuthenticationPrincipal principal: User): UserResponseDTO {
        return principal.toDTO()
    }
}