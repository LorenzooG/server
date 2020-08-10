package store.wckd.server.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtFilter extends BasicAuthenticationFilter {
    private static final String AUTHENTICATION_HEADER = "Authorization";
    private static final String AUTHENTICATION_HEADER_PREFIX = "Bearer ";

    private UserDetailsService userDetailsService;
    private Algorithm jwtAlgorithm;

    public JwtFilter(UserDetailsService userDetailsService, Algorithm jwtAlgorithm, AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.userDetailsService = userDetailsService;
        this.jwtAlgorithm = jwtAlgorithm;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        String jwtToken = request.getHeader(AUTHENTICATION_HEADER);

        if(jwtToken == null) jwtToken = "";

        jwtToken = jwtToken.replace(AUTHENTICATION_HEADER_PREFIX, "");

        SecurityContextHolder
                .getContext()
                .setAuthentication(getAuthenticationFromJwtToken(jwtToken));

        chain.doFilter(request, response);
    }

    private Authentication getAuthenticationFromJwtToken(String jwtTokenString) {
        DecodedJWT jwt = JWT.decode(jwtTokenString);
        String username = jwt.getPayload();

        return new UsernamePasswordAuthenticationToken(username, jwt);
    }
}
