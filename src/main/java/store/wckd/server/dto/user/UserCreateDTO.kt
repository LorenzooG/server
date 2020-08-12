package store.wckd.server.dto.user

data class UserCreateDTO(
        val username: String,
        val email: String,
        val password: String
)
