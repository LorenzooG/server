package store.wckd.server.factory;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface Factory<T> {

    Flux<T> createMany(int amount);
    Mono<T> createOne();

}
