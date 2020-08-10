package store.wckd.server.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import store.wckd.server.entity.User;

import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.issuer}")
    private String issuer;

    private Algorithm jwtAlgorithm;

    public JwtService(Algorithm jwtAlgorithm) {
        this.jwtAlgorithm = jwtAlgorithm;
    }

    public DecodedJWT decodeJwt(String jwtString) {
        return JWT.decode(jwtString);
    }

    public String encodeJwt(User user) {
        return JWT.create()
                .withIssuedAt(Date.from(Instant.now()))
                .withIssuer(issuer)
                .withSubject(user.getUsername())
                .sign(jwtAlgorithm);
    }

}
