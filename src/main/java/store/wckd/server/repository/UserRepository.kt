package store.wckd.server.repository

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import store.wckd.server.entity.User
import java.util.*

@Repository
interface UserRepository : PagingAndSortingRepository<User, Long> {
    fun findByUsername(username: String): Optional<User>
}