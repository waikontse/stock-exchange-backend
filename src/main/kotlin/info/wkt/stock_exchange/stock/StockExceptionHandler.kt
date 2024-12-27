package info.wkt.stock_exchange.stock

import info.wkt.stock_exchange.exceptions.*
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice(basePackages = ["info.wkt.stock_exchange.stock"])
class StockExceptionHandler {
    @ExceptionHandler(StockAlreadyExistsException::class, produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    fun handleStockAlreadyExistsExxeption(ex: StockAlreadyExistsException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.unprocessableEntity().build()
    }

    @ExceptionHandler(ExchangeAlreadyExistsException::class, produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    fun handleExchangeAlreadyExistsException(ex: ExchangeAlreadyExistsException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.unprocessableEntity().build()
    }

    @ExceptionHandler(ExchangeNotFoundException::class, produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleExchangeNotFoundException(ex: ExchangeNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.notFound().build()
    }

    @ExceptionHandler(StockNotFoundException::class, produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleStockNotFoundException(ex: StockNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.notFound().build()
    }

    @ExceptionHandler(BusinessExceptions::class, produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    fun handleBusinessException(ex: BusinessExceptions): ResponseEntity<ErrorResponse> {
        return ResponseEntity.unprocessableEntity().build()
    }

    @ExceptionHandler(MethodArgumentNotValidException::class, produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleRuntimeException(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.badRequest().build()
    }
}