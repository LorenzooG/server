package store.wckd.server.auth

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import store.wckd.server.auth.filter.JwtFilter
import store.wckd.server.auth.provider.AuthenticationProviderImpl
import store.wckd.server.auth.session.SessionAuthenticationStrategyImpl
import store.wckd.server.auth.userdetailservice.UserDetailsServiceImpl
import store.wckd.server.service.JwtService
import store.wckd.server.service.UserService

@Configuration
@EnableWebSecurity
@EnableGlobalAuthentication
class SecurityConfigurerAdapter(
        val userService: UserService,
        val userDetailsService: UserDetailsServiceImpl,
        val jwtService: JwtService,
        val passwordEncoder: PasswordEncoder
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http {
            cors { disable() }
            csrf { disable() }

            authorizeRequests {
                authorize("/me", authenticated)
                authorize(anyRequest, permitAll)
            }

            sessionManagement {
                sessionCreationPolicy = STATELESS
            }

            http.addFilter(JwtFilter(jwtService, authenticationManager()))
            http.addFilter(configureUsernamePasswordAuthenticationFilter())
        }
    }

    fun configureUsernamePasswordAuthenticationFilter() =
            UsernamePasswordAuthenticationFilter().apply {
                setAuthenticationManager(authenticationManager())
                setSessionAuthenticationStrategy(SessionAuthenticationStrategyImpl(jwtService))
            }

    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.run {
            userDetailsService(userDetailsService).apply {
                passwordEncoder(passwordEncoder)
            }

            eraseCredentials(true)

            authenticationProvider(AuthenticationProviderImpl(userService, passwordEncoder))
        }
    }

    @Bean("authenticationManager")
    override fun userDetailsServiceBean(): UserDetailsService = super.userDetailsServiceBean()

    @Bean("authenticationManager")
    override fun authenticationManagerBean(): AuthenticationManager = super.authenticationManagerBean()
}