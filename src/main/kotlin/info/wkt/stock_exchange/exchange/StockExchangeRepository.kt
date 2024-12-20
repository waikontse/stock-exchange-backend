package info.wkt.stock_exchange.exchange

import info.wkt.stock_exchange.domain.StockExchange
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface StockExchangeRepository: JpaRepository<StockExchange, Long> {
    fun existsByName(name: String): Boolean

    fun findByName(name: String): StockExchange?
}