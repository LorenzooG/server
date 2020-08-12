package store.wckd.server.factory;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import store.wckd.server.entity.User;
import store.wckd.server.repository.UserRepository;

public class UserFactory implements Factory<User> {
    private final UserRepository userRepository;

    public UserFactory(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Flux<User> createMany(int amount) {
        //noinspection ReactiveStreamsNullableInLambdaInTransform
        return Flux
                .range(1, amount)
                .map(id -> createOne().block());
    }

    @Override
    public Mono<User> createOne() {
        return Mono.just(userRepository.save(new User(
                0L,
                "fake username",
                "fake email",
                "fake password"
        )));
    }
}
