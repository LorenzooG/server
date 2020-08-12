package store.wckd.server.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import store.wckd.server.entity.User
import java.time.Instant
import java.util.*

/**
 * Encodes and decodes JWT based in a user
 */
@Service
class JwtService(
        private val userService: UserService,
        private val jwtAlgorithm: Algorithm
) {
    @Value("\${jwt.issuer}")
    private lateinit var issuer: String

    /**
     * Decodes a jwt and find a user based in id found in that
     *
     * @return the found user
     */
    suspend fun decodeJwtToUser(jwt: String): User =
            JWT.decode(jwt)
                    .subject
                    .toLongOrNull()
                    .let {
                        userService.findById(it ?: 0)
                    }

    /**
     * Encodes a jwt based on user [user]
     *
     * @return the encoded string jwt
     */
    fun encodeJwt(user: User): String =
            JWT.create()
                    .withIssuedAt(Date.from(Instant.now()))
                    .withIssuer(issuer)
                    .withSubject(user.id.toString())
                    .sign(jwtAlgorithm)
}