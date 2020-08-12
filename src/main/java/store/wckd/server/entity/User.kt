package store.wckd.server.entity

import store.wckd.server.dto.user.UserResponseDTO
import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long = 0L,

        @Column
        val username: String,

        @Column
        val email: String,

        @Column
        val password: String
) {
    fun toDTO(): UserResponseDTO {
        return UserResponseDTO(id, username, email)
    }
}