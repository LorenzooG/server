package store.wckd.server.dto.user

data class UserUpdateDTO (
    val username: String,
    val email: String,
    val password: String
)