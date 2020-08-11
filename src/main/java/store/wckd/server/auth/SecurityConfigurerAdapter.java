package store.wckd.server.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import store.wckd.server.auth.filter.JwtFilter;
import store.wckd.server.auth.provider.AuthenticationProviderImpl;
import store.wckd.server.auth.session.SessionAuthenticationStrategyImpl;
import store.wckd.server.auth.userdetailservice.UserDetailsServiceImpl;
import store.wckd.server.service.JwtService;
import store.wckd.server.service.UserService;

@Configuration
@EnableWebSecurity
@EnableGlobalAuthentication
public class SecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfigurerAdapter(UserService userService, UserDetailsServiceImpl userDetailsService, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
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
                .addFilter(new JwtFilter(userService, jwtService, authenticationManager()))
                .addFilter(configureUsernamePasswordAuthenticationFilter());
    }

    public UsernamePasswordAuthenticationFilter configureUsernamePasswordAuthenticationFilter() throws Exception {
        UsernamePasswordAuthenticationFilter filter = new UsernamePasswordAuthenticationFilter();

        filter.setAuthenticationManager(authenticationManager());
        filter.setSessionAuthenticationStrategy(new SessionAuthenticationStrategyImpl(jwtService));

        return filter;
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
                .authenticationProvider(new AuthenticationProviderImpl(userService, passwordEncoder));
    }

    @Override
    @Bean("authenticationManager")
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return super.userDetailsServiceBean();
    }

    @Override
    @Bean("authenticationManager")
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
