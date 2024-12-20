package info.wkt.stock_exchange.exchange

import info.wkt.stock_exchange.domain.StockRegistration
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StockRegistrationRepository: JpaRepository<StockRegistration, Long> {
}