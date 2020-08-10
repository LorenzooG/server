package store.wckd.server.auth;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import store.wckd.server.service.UserService;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        store.wckd.server.entity.User user = userService
                .findByUsername(username)
                .block();

        if(user == null) throw new UsernameNotFoundException("The system could not find a user with username " + username);

        return new User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptySet()
        );
    }

}
