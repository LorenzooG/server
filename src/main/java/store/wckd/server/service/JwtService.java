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

    private final UserService userService;
    private final Algorithm jwtAlgorithm;

    public JwtService(UserService userService, Algorithm jwtAlgorithm) {
        this.userService = userService;
        this.jwtAlgorithm = jwtAlgorithm;
    }

    public User decodeJwtToUser(String jwtString) {
        long id = 0;

        try {
            id = Long.parseLong(JWT.decode(jwtString).getSubject());
        } catch (Exception ignored) {
        }

        return userService.findById(id).block();
    }

    public String encodeJwt(User user) {
        return JWT.create()
                .withIssuedAt(Date.from(Instant.now()))
                .withIssuer(issuer)
                .withSubject(String.valueOf(user.getId()))
                .sign(jwtAlgorithm);
    }

}
