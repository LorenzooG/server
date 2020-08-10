package store.wckd.server.auth;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthenticationProviderImpl implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationProviderImpl(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication contextAuthentication = securityContext.getAuthentication();

        // will search in the context authentication, if is not null then will use it instead of repeat the same process of check.
        if(contextAuthentication != null) return contextAuthentication;

        UsernamePasswordAuthenticationToken credentialsToken = (UsernamePasswordAuthenticationToken) authentication;

        // find user by credentials's username
        UserDetails userDetails = userDetailsService.loadUserByUsername(credentialsToken.getName());

        Object credentials = credentialsToken.getCredentials();
        if(credentials == null) credentials = "";

        String password = userDetails.getPassword();
        if(password == null) password = "";

        if (!passwordEncoder.matches(credentials.toString(), password))
            throw new BadCredentialsException("Your password/username is incorrect!");

        // set the authentication to the context
        securityContext.setAuthentication(authentication);

        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }
}
