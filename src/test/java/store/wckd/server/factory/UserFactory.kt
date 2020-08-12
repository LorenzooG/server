package store.wckd.server.factory

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import store.wckd.server.entity.User
import store.wckd.server.repository.UserRepository

class UserFactory(private val userRepository: UserRepository) : Factory<User> {
    override fun createMany(amount: Int): Flux<User> {
        return Flux
                .range(1, amount)
                .map { createOne().block()!! }
    }

    override fun createOne(): Mono<User> =
            Mono.just(userRepository.save(User(
                    username = "fake username",
                    email = "fake email",
                    password = "fake password"
            )))

}