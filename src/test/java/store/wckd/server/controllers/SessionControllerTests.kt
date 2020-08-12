@file:Suppress("LeakingThis")

package store.wckd.server.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.anyString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import store.wckd.server.auth.JwtAuthenticationConverter.Companion.AUTHORIZATION_HEADER_PREFIX
import store.wckd.server.auth.JwtAuthenticationConverter.Companion.AUTHORIZATION_HEADER
import store.wckd.server.controller.SessionsController
import store.wckd.server.dto.LoginRequestDTO
import store.wckd.server.dto.LoginResponseDTO
import store.wckd.server.dto.UserResponseDTO
import store.wckd.server.entity.User
import store.wckd.server.factory.Factory
import store.wckd.server.factory.UserFactory
import store.wckd.server.repository.UserRepository
import store.wckd.server.service.JwtService
import org.mockito.Mockito.`when` as every

@SpringBootTest
@AutoConfigureWebTestClient
class SessionControllerTests @Autowired constructor(
        private val webTestClient: WebTestClient,
        private val jwtService: JwtService,

        userRepository: UserRepository
) {
    @MockBean
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    private val userFactory: Factory<User> = UserFactory(userRepository)

    @Test
    @DisplayName("It should return the jwt token when request ${SessionsController.LOGIN_ENDPOINT} with correct credentials")
    fun testLogin() {
        every(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(true)

        val user = runBlocking { userFactory.createOne() }

        webTestClient.post()
                .uri(SessionsController.LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(LoginRequestDTO(
                        username = user.username,
                        password = ""
                )))
                .exchange()
                .expectStatus().isOk
                .expectBody<LoginResponseDTO>().also {
                    it.consumeWith { response ->
                        Assertions.assertEquals(user, runBlocking {
                            jwtService.decodeJwtToUser(response.responseBody!!.jwtToken)
                        })
                    }
                }
    }

    @Test
    @DisplayName("It should return the current user when request ${SessionsController.SESSION_ENDPOINT} with correct JWT Token")
    fun testAuthorization() {
        val user = runBlocking { userFactory.createOne() }
        val jwt = jwtService.encodeJwt(user)

        webTestClient.get()
                .uri(SessionsController.SESSION_ENDPOINT)
                .header(AUTHORIZATION_HEADER, "$AUTHORIZATION_HEADER_PREFIX$jwt")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody<UserResponseDTO>().isEqualTo(user.toDTO())
    }

    companion object {
        private val objectMapper = jacksonObjectMapper()
    }
}