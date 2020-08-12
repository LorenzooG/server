package store.wckd.server.configuration

import com.auth0.jwt.algorithms.Algorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
class SecurityConfiguration {
    @Value("\${jwt.secret}")
    private lateinit var secret: String

    @Value("\${password.encoder.strength}")
    private var passwordEncoderStrength = 8

    private lateinit var jwtAlgorithm: Algorithm
    private lateinit var passwordEncoder: PasswordEncoder

    // will set lazy the jwt algorithm lazy
    @Autowired
    fun setup() {
        jwtAlgorithm = Algorithm.HMAC512(secret)
        passwordEncoder = BCryptPasswordEncoder(passwordEncoderStrength)
    }

    @Bean("jwtAlgorithm")
    fun jwtAlgorithmBean() = jwtAlgorithm

    @Bean("passwordEncoder")
    fun passwordEncoderBean() = passwordEncoder

    @Bean("securityWebFilter")
    fun securityFilterChainBean(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
                .csrf { it.disable() }
                .httpBasic { it.disable() }
                .logout { it.disable() }
                .formLogin { it.disable() }
                .authorizeExchange { spec ->
                    spec.pathMatchers(HttpMethod.GET, "/me").authenticated()

                    spec.anyExchange().permitAll()
                }
                .build()
    }
}