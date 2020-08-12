package store.wckd.server.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import store.wckd.server.entity.User
import java.time.Instant
import java.util.*
import javax.persistence.EntityNotFoundException

@Service
class JwtService(
        private val userService: UserService,
        private val jwtAlgorithm: Algorithm
) {
    @Value("\${jwt.issuer}")
    private lateinit var issuer: String

    fun decodeJwtToUser(jwt: String): User =
            JWT.decode(jwt)
                    .subject
                    .toLongOrNull()
                    .let {
                        userService.findById(it ?: 0).block() ?: throw EntityNotFoundException()
                    }

    fun encodeJwt(user: User): String =
            JWT.create()
                    .withIssuedAt(Date.from(Instant.now()))
                    .withIssuer(issuer)
                    .withSubject(user.id.toString())
                    .sign(jwtAlgorithm)
}