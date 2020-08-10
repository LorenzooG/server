package store.wckd.server.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import store.wckd.server.dto.UserCreateDTO;
import store.wckd.server.dto.UserUpdateDTO;
import store.wckd.server.entity.User;
import store.wckd.server.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Flux<User> findAll(int page) {
        return Flux.fromIterable(userRepository.findAll());
    }

    public Mono<User> create(UserCreateDTO userCreateDTO) {
        User user = new User(
                userCreateDTO.getUsername(),
                userCreateDTO.getEmail(),
                userCreateDTO.getPassword()
        );

        return Mono.just(userRepository.save(user));
    }

    public Mono<User> findByUsername(String username) {
        return Mono.just(userRepository.findByUsername(username));
    }

    public Mono<User> findById(long id) {
        return Mono.justOrEmpty(userRepository.findById(id));
    }

    public Mono<User> updateById(long id, UserUpdateDTO userUpdateDTO) {
        User user = new User(
                id,
                userUpdateDTO.getUsername(),
                userUpdateDTO.getEmail(),
                userUpdateDTO.getPassword()
        );

        return Mono.just(userRepository.save(user));
    }

    public Mono<Void> deleteById(long id) {
        userRepository.deleteById(id);

        return Mono.empty();
    }

}
