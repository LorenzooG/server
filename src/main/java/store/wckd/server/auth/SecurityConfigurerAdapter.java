package store.wckd.server.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import store.wckd.server.service.JwtService;

@Configuration
@EnableWebSecurity
@EnableGlobalAuthentication
public class SecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfigurerAdapter(UserDetailsServiceImpl userDetailsService, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // cors and csrf
                .cors()
                .and()
                .csrf().disable()

                // authorization
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/me").authenticated()
                .anyRequest().permitAll()
                .and()

                // session management
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                // authorization filter
                .addFilter(new JwtFilter(userDetailsService, jwtService, authenticationManager()))
                .addFilter(new UsernamePasswordAuthenticationFilter());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                // dao settings
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()

                // general settings
                .eraseCredentials(true)

                // authentication provider
                .authenticationProvider(new AuthenticationProviderImpl(userDetailsService, passwordEncoder));
    }

    @Override
    @Bean("authenticationManager")
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
