package info.wkt.stock_exchange.stock

import info.wkt.stock_exchange.domain.Stock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StockRepository: JpaRepository<Stock, Long> {
    fun existsByName(name: String): Boolean

    fun findByName(name: String): Stock?
}