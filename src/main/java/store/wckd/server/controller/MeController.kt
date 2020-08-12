package store.wckd.server.controller

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import store.wckd.server.dto.UserResponseDTO
import store.wckd.server.entity.User

@RestController
class MeController {
    @GetMapping("/me")
    fun me(): UserResponseDTO {
        val authentication = SecurityContextHolder.getContext().authentication

        return (authentication.principal as User).toDTO()
    }
}