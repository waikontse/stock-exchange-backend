package info.wkt.stock_exchange.domain

import jakarta.persistence.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Entity(name = "stock_exchange")
class StockExchange(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @Column
    val description: String,

    @Column
    var liveInMarket: Boolean = false,

    @OneToMany(mappedBy = "exchange", cascade = [CascadeType.PERSIST, CascadeType.REFRESH])
    val registrations: Set<StockRegistration> = setOf(),
) {
    companion object {
        val log: Logger = LoggerFactory.getLogger(StockExchange::class.java)
    }

    fun updateIsLiveInMarket() {
        log.info("Updating is live in market for exchange $name with stock size: ${registrations.size}")

        if (registrations.size >= 5) {
            liveInMarket = true
        }
    }

    fun hasRegisteredStock(stock: Stock): Boolean {
        return registrations.map { it.stock }.contains(stock)
    }
}