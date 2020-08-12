package store.wckd.server.dto

data class UserCreateDTO(
        val username: String,
        val email: String,
        val password: String
)
