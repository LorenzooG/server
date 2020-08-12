package store.wckd.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kotlin.coroutine.EnableCoroutine

@SpringBootApplication
@EnableCoroutine
class ServerApplication

fun main(vararg args: String) {
    runApplication<ServerApplication>(*args)
}