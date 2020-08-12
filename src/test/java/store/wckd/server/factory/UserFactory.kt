package store.wckd.server.factory

import store.wckd.server.entity.User
import store.wckd.server.repository.UserRepository

class UserFactory(private val userRepository: UserRepository) : Factory<User> {
    override suspend fun createMany(amount: Int): Iterable<User> = (1..amount).map {
        createOne()
    }

    override suspend fun createOne(): User = userRepository.save(User(
            username = "fake username",
            email = "fake email",
            password = "fake password"
    ))
}