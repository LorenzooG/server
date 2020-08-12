package store.wckd.server.exception

import org.springframework.http.HttpStatus
import java.lang.RuntimeException

sealed class HttpException(message: String, val code: HttpStatus) : RuntimeException(message)

class EntityNotFoundException : HttpException("Not found the requested entity", HttpStatus.NOT_FOUND)