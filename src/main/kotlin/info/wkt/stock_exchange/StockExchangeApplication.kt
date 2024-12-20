package info.wkt.stock_exchange

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StockExchangeApplication

fun main(args: Array<String>) {
	runApplication<StockExchangeApplication>(*args)
}
