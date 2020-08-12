package store.wckd.server.factory

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface Factory<T> {
    suspend fun createMany(amount: Int): Iterable<T>
    suspend fun createOne(): T
}