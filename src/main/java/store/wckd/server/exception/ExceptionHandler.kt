package store.wckd.server.exception

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.withContext
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebExchange
import reactor.kotlin.core.publisher.toMono

@Configuration
class ExceptionHandler : ErrorWebExceptionHandler {
    companion object {
        private val objectMapper = jacksonObjectMapper()
    }

    override fun handle(exchange: ServerWebExchange, ex: Throwable) = mono {
        val response = exchange.response

        val code = if (ex is HttpException)
            ex.code
        else
            HttpStatus.INTERNAL_SERVER_ERROR

        @Suppress("BlockingMethodInNonBlockingContext")
        val json = withContext(Dispatchers.IO) {
            objectMapper.writeValueAsBytes(ex)
        }

        val dataBuffer = response.bufferFactory().wrap(json)

        response.apply { statusCode = code }
                .writeWith(dataBuffer.toMono())
                .awaitSingle()
    }
}