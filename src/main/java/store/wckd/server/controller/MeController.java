package store.wckd.server.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import store.wckd.server.entity.User;
import store.wckd.server.service.UserService;

@RestController
public class MeController {

    private final UserService userService;

    public MeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public User me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(!(authentication instanceof UsernamePasswordAuthenticationToken))
            throw new RuntimeException("Could not get the current session with that token");

        UsernamePasswordAuthenticationToken credentialsToken = (UsernamePasswordAuthenticationToken) authentication;

        Object usernameNullable = credentialsToken.getPrincipal();
        if(usernameNullable == null) usernameNullable = "";

        String username = usernameNullable.toString();

        return userService.findByUsername(username).block();
    }

}
