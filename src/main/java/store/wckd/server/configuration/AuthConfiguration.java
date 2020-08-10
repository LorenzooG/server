package store.wckd.server.configuration;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import store.wckd.server.auth.AuthenticationProviderImpl;
import store.wckd.server.auth.JwtFilter;
import store.wckd.server.auth.UserDetailsServiceImpl;
import store.wckd.server.service.JwtService;

@Configuration
@EnableWebSecurity
public class AuthConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${password.encoder.strength}")
    private int passwordEncoderStrength;

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    private Algorithm jwtAlgorithm;
    private PasswordEncoder passwordEncoder;

    public AuthConfiguration(UserDetailsServiceImpl userDetailsService, JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    // will set lazy the jwt algorithm lazy
    @Autowired
    public void setup() {
        this.jwtAlgorithm = Algorithm.HMAC512(secret);
        this.passwordEncoder = new BCryptPasswordEncoder(passwordEncoderStrength);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // cors and csrf
                .cors()
                .and().csrf().disable()

                // authorization
                .authorizeRequests()
                .anyRequest().permitAll()

                // session management
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // authorization filter
                .and().addFilter(new JwtFilter(userDetailsService, jwtService, authenticationManager()))
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

    @Bean("jwtAlgorithm")
    public Algorithm jwtAlgorithmBean() {
        return jwtAlgorithm;
    }

    @Bean("passwordEncoder")
    public PasswordEncoder passwordEncoderBean() {
        return passwordEncoder;
    }

    @Override
    @Bean("authenticationManager")
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
