package store.wckd.server.configuration;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import store.wckd.server.auth.JwtFilter;
import store.wckd.server.auth.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class AuthConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${password.encoder.strength}")
    private int passwordEncoderStrength;

    private final UserDetailsService userDetailsService;

    private Algorithm jwtAlgorithm;

    public AuthConfiguration(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // will set lazy the jwt algorithm lazy
    @Autowired
    public void setupAlgorithm() {
        this.jwtAlgorithm = Algorithm.HMAC512(secret);
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
                .and().addFilter(new JwtFilter(userDetailsService, jwtAlgorithm, authenticationManager()));
    }

    @Bean("jwtAlgorithm")
    public Algorithm jwtAlgorithmBean() {
        return jwtAlgorithm;
    }

    @Bean("passwordEncoder")
    public PasswordEncoder passwordEncoderBean() {
        return new BCryptPasswordEncoder(passwordEncoderStrength);
    }

    @Override
    @Bean("authenticationManager")
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
