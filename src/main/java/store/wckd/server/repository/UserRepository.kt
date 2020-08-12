package store.wckd.server.repository

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import store.wckd.server.entity.User
import java.util.*

/**
 * User JPA Repository. Provides users paginated.
 */
@Repository
interface UserRepository : PagingAndSortingRepository<User, Long> {
    /**
     * Find a user by its username [username]
     *
     * @return if could not found a empty optional, else return the user optional-wrapped
     */
    fun findByUsername(username: String): Optional<User>
}