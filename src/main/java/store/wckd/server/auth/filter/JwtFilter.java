package store.wckd.server.auth.filter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import store.wckd.server.entity.User;
import store.wckd.server.service.JwtService;
import store.wckd.server.service.UserService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtFilter extends BasicAuthenticationFilter {
    public static final String AUTHENTICATION_HEADER = "Authorization";
    public static final String AUTHENTICATION_HEADER_PREFIX = "Bearer ";

    private final UserService userService;
    private final JwtService jwtService;

    public JwtFilter(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        try {
            String jwtToken = request.getHeader(AUTHENTICATION_HEADER);

            if(jwtToken == null) jwtToken = "";

            jwtToken = jwtToken.replace(AUTHENTICATION_HEADER_PREFIX, "");

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(getAuthenticationFromJwtToken(jwtToken));
        } catch (AuthenticationException exception) {
            if(isIgnoreFailure()) {
                chain.doFilter(request, response);
            } else {
                throw exception;
            }

            return;
        }

        chain.doFilter(request, response);
    }

    private Authentication getAuthenticationFromJwtToken(String jwtTokenString) {
        try {
            return new UsernamePasswordAuthenticationToken(jwtService.decodeJwtToUser(jwtTokenString), "");
        } catch (JWTDecodeException exception) {
            // create an empty authentication token if has any error
            return new UsernamePasswordAuthenticationToken("", "");
        }
    }
}
