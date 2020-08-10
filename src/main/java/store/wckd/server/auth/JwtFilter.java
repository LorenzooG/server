package store.wckd.server.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import store.wckd.server.service.JwtService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtFilter extends BasicAuthenticationFilter {
    public static final String AUTHENTICATION_HEADER = "Authorization";
    public static final String AUTHENTICATION_HEADER_PREFIX = "Bearer ";

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public JwtFilter(
            UserDetailsService userDetailsService,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        super(authenticationManager);
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
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

        authenticationManager.authenticate(getAuthenticationFromJwtToken(jwtToken));

        chain.doFilter(request, response);
    }

    private Authentication getAuthenticationFromJwtToken(String jwtTokenString) {
        String username = jwtService
                .decodeJwt(jwtTokenString)
                .getSubject();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword());
    }
}
