package store.wckd.server.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import store.wckd.server.dto.UserCreateDTO
import store.wckd.server.dto.UserUpdateDTO
import store.wckd.server.entity.User
import store.wckd.server.repository.UserRepository

@Service
class UserService(private val userRepository: UserRepository) {
    fun findAll(page: Int): Flux<User> {
        return Flux.fromIterable(userRepository.findAll())
    }

    fun create(userCreateDTO: UserCreateDTO): Mono<User> {
        val user = User(
                username = userCreateDTO.username,
                email = userCreateDTO.email,
                password = userCreateDTO.password
        )

        return Mono.just(userRepository.save(user))
    }

    fun findByUsername(username: String): Mono<User> {
        return Mono.justOrEmpty(userRepository.findByUsername(username))
    }

    fun findById(id: Long): Mono<User> {
        return Mono.justOrEmpty(userRepository.findById(id))
    }

    fun updateById(id: Long, userUpdateDTO: UserUpdateDTO): Mono<User> {
        val user = User(
                id,
                userUpdateDTO.username,
                userUpdateDTO.email,
                userUpdateDTO.password
        )
        return Mono.just(userRepository.save(user))
    }

    fun deleteById(id: Long): Mono<Void> {
        userRepository.deleteById(id)

        return Mono.empty()
    }

}