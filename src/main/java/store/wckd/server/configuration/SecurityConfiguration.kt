package store.wckd.server.configuration;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfiguration {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${password.encoder.strength}")
    private int passwordEncoderStrength;

    private Algorithm jwtAlgorithm;
    private PasswordEncoder passwordEncoder;

    // will set lazy the jwt algorithm lazy
    @Autowired
    public void setup() {
        this.jwtAlgorithm = Algorithm.HMAC512(secret);
        this.passwordEncoder = new BCryptPasswordEncoder(passwordEncoderStrength);
    }

    @Bean("jwtAlgorithm")
    public Algorithm jwtAlgorithmBean() {
        return jwtAlgorithm;
    }

    @Bean("passwordEncoder")
    public PasswordEncoder passwordEncoderBean() {
        return passwordEncoder;
    }

}
