package store.wckd.server.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import store.wckd.server.service.JwtService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtFilter extends BasicAuthenticationFilter {
    private static final String AUTHENTICATION_HEADER = "Authorization";
    private static final String AUTHENTICATION_HEADER_PREFIX = "Bearer ";

    private UserDetailsService userDetailsService;
    private JwtService jwtService;

    public JwtFilter(
            UserDetailsService userDetailsService,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        super(authenticationManager);
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
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
        String username = jwtService
                .decodeJwt(jwtTokenString)
                .getSubject();

        return new UsernamePasswordAuthenticationToken(username, jwtTokenString);
    }
}
