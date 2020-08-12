package store.wckd.server.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import store.wckd.server.dto.user.UserCreateDTO
import store.wckd.server.dto.user.UserUpdateDTO
import store.wckd.server.entity.User
import store.wckd.server.exception.EntityNotFoundException
import store.wckd.server.repository.UserRepository

@Service
class UserService(private val userRepository: UserRepository) {
    suspend fun findAll(page: Int): Page<User> {
        return userRepository.findAll(PageRequest.of(page, 15))
    }

    suspend fun create(userCreateDTO: UserCreateDTO): User {
        val user = User(
                username = userCreateDTO.username,
                email = userCreateDTO.email,
                password = userCreateDTO.password
        )

        return userRepository.save(user)
    }

    suspend fun findByUsername(username: String): User {
        return userRepository.findByUsername(username).orElseThrow(::EntityNotFoundException)
    }

    suspend fun findById(id: Long): User {
        return userRepository.findById(id).orElseThrow(::EntityNotFoundException)
    }

    suspend fun updateById(id: Long, userUpdateDTO: UserUpdateDTO): User {
        val user = User(
                id,
                userUpdateDTO.username,
                userUpdateDTO.email,
                userUpdateDTO.password
        )

        return userRepository.save(user)
    }

    suspend fun deleteById(id: Long) {
        userRepository.deleteById(id)
    }
}