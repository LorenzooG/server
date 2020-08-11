package store.wckd.server.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import store.wckd.server.entity.User;
import store.wckd.server.service.UserService;

@RestController
public class MeController {

    @GetMapping("/me")
    public User me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return (User) authentication.getPrincipal();
    }

}
