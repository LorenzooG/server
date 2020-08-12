package store.wckd.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ServerApplication

fun main(vararg args: String) {
    runApplication<ServerApplication>(*args)
}