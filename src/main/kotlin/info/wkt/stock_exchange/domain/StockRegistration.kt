package info.wkt.stock_exchange.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class StockRegistration(
    @Id @GeneratedValue val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "exchange_id")
    val exchange: StockExchange,

    @ManyToOne
    @JoinColumn(name = "stock_id")
    val stock: Stock,

    @Column(nullable = false)
    val registeredAt: LocalDateTime
    ) {
}