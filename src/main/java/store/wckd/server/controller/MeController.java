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

        Object usernameAsObject = authentication.getPrincipal();
        String username = usernameAsObject == null ? "" : usernameAsObject.toString();

        return userService.findByUsername(username).block();
    }

}
