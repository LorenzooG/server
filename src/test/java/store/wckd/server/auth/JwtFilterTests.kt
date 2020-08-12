package store.wckd.server.auth

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import store.wckd.server.auth.filter.JwtFilter
import store.wckd.server.entity.User
import store.wckd.server.factory.Factory
import store.wckd.server.factory.UserFactory
import store.wckd.server.repository.UserRepository
import store.wckd.server.service.JwtService
import store.wckd.server.service.UserService
import org.mockito.Mockito.`when` as every

@SpringBootTest
@AutoConfigureMockMvc
class JwtFilterTests @Autowired constructor(
        private val jwtService: JwtService,
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
    @DisplayName("It should not use jwt token if request a route that is permitted")
    fun testAuthenticationFilterNotActivate() {
        mockMvc.get("/") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound }
        }
    }

    @Test
    @DisplayName("It should login when JWT Token is valid")
    fun testAuthenticationFilter() {
        every(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(true)

        val userMono = userFactory.createOne()

        every(userService.findById(anyLong()))
                .thenReturn(userMono)

        val user = userMono.block()!!

        val jwtToken = jwtService.encodeJwt(user)
        val userJson = objectMapper.writeValueAsString(user.toDTO())

        mockMvc.get(TESTING_ENDPOINT) {
            header(JwtFilter.AUTHENTICATION_HEADER, jwtToken)

            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk }
            content { json(userJson) }
        }
    }

    companion object {
        private val objectMapper = ObjectMapper()

        private const val TESTING_ENDPOINT = "/me"
    }
}