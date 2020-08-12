package store.wckd.server.auth

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.anyString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import store.wckd.server.entity.User
import store.wckd.server.factory.Factory
import store.wckd.server.factory.UserFactory
import store.wckd.server.repository.UserRepository
import store.wckd.server.service.UserService
import org.mockito.Mockito.`when` as every

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationTests @Autowired constructor(
        private val mockMvc: MockMvc,

        userRepository: UserRepository
) {
    @MockBean
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @MockBean
    @Autowired
    private lateinit var userService: UserService

    private val userFactory: Factory<User> = UserFactory(userRepository)

    @Test
    @DisplayName("It should login properly and return a valid jwt token to be used in the headers")
    @Throws(Exception::class)
    fun testLogin() {
        every(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(true)

        val userMono = userFactory.createOne()

        every(userService.findByUsername(anyString()))
                .thenReturn(userMono)

        val user = userMono.block()!!

        mockMvc.post("/login?username=${user.username}&password=${user.password}") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk }
        }
    }

    companion object {
        private val objectMapper = ObjectMapper()
        private const val TESTING_ENDPOINT = "/me"
    }
}