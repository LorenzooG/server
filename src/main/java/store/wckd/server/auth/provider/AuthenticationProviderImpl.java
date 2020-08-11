package store.wckd.server.auth.provider;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import store.wckd.server.entity.User;
import store.wckd.server.service.UserService;

public class AuthenticationProviderImpl implements AuthenticationProvider {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public AuthenticationProviderImpl(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication contextAuthentication = securityContext.getAuthentication();

        // will search in the context authentication, if is not null then will use it instead of repeat the same process of check.
        if (contextAuthentication != null) return contextAuthentication;

        UsernamePasswordAuthenticationToken credentialsToken = (UsernamePasswordAuthenticationToken) authentication;

        Object usernameAsObject = credentialsToken.getPrincipal();
        String username = usernameAsObject == null ? "" : usernameAsObject.toString();

        // find user by credentials' username
        User user = userService.findByUsername(username).block();
        if (user == null) throw new UsernameNotFoundException("Could not find a user with username " + username);

        Object password = credentialsToken.getCredentials();
        if (password == null) password = "";

        String hashedPassword = user.getPassword();
        if (hashedPassword == null) hashedPassword = "";

        if (!passwordEncoder.matches(password.toString(), hashedPassword))
            throw new BadCredentialsException("Your password/username is incorrect!");

        // set the authentication to the context
        securityContext.setAuthentication(authentication);

        return new UsernamePasswordAuthenticationToken(user, password);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }
}
