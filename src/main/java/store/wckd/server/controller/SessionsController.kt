package store.wckd.server.controller

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import store.wckd.server.dto.LoginRequestDTO
import store.wckd.server.dto.LoginResponseDTO
import store.wckd.server.dto.UserResponseDTO
import store.wckd.server.entity.User
import store.wckd.server.service.JwtService
import store.wckd.server.service.UserService

@RestController
class SessionsController(
        private val userService: UserService,
        private val jwtService: JwtService,
        private val passwordEncoder: PasswordEncoder
) {
    @PostMapping("/login")
    suspend fun login(@RequestBody body: LoginRequestDTO): ResponseEntity<*> {
        val user: User = userService.findByUsername(body.username).awaitSingle()

        if(!passwordEncoder.matches(body.password, user.password))
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(mapOf(
                            "message" to "Invalid password or username."
                    ))

        return ResponseEntity.ok(LoginResponseDTO(jwtService.encodeJwt(user)))
    }

    @GetMapping("/session")
    fun session(): UserResponseDTO {
        val authentication = SecurityContextHolder.getContext().authentication

        return (authentication.principal as User).toDTO()
    }
}