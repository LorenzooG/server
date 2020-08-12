package store.wckd.server.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import store.wckd.server.dto.user.UserCreateDTO
import store.wckd.server.dto.user.UserUpdateDTO
import store.wckd.server.entity.User
import store.wckd.server.exception.EntityNotFoundException
import store.wckd.server.repository.UserRepository

/**
 * Class that provides users for you use
 */
@Service
class UserService(private val userRepository: UserRepository) {
    /**
     * Find all paginated users in page number [page]
     *
     * @return a paginated result with size 15
     */
    suspend fun findAll(page: Int): Page<User> {
        return userRepository.findAll(PageRequest.of(page, 15))
    }

    /**
     * Create a new user based on [userCreateDTO]
     *
     * @return the created user
     */
    suspend fun create(userCreateDTO: UserCreateDTO): User {
        val user = User(
                username = userCreateDTO.username,
                email = userCreateDTO.email,
                password = userCreateDTO.password
        )

        return userRepository.save(user)
    }

    /**
     * Find user by its username [username]
     *
     * @return the user found
     * @throws EntityNotFoundException if could not found user
     */
    suspend fun findByUsername(username: String): User {
        return userRepository.findByUsername(username).orElseThrow(::EntityNotFoundException)
    }

    /**
     * Find user by its id [id]
     *
     * @return the user found
     * @throws EntityNotFoundException if could not found user
     */
    suspend fun findById(id: Long): User {
        return userRepository.findById(id).orElseThrow(::EntityNotFoundException)
    }

    /**
     * Update by [id] or insert in database if do not exists based on [userUpdateDTO]
     *
     * @return the updated/created user
     */
    suspend fun updateById(id: Long, userUpdateDTO: UserUpdateDTO): User {
        val user = User(
                id,
                userUpdateDTO.username,
                userUpdateDTO.email,
                userUpdateDTO.password
        )

        return userRepository.save(user)
    }

    /**
     * Delete user by its id [id]
     *
     * @return nothing
     */
    suspend fun deleteById(id: Long) {
        userRepository.deleteById(id)
    }
}