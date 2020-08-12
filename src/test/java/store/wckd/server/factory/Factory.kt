package store.wckd.server.factory

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface Factory<T> {
    fun createMany(amount: Int): Flux<T>
    fun createOne(): Mono<T>
}