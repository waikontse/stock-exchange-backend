package info.wkt.stock_exchange.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class StockRegistration(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(cascade = [CascadeType.PERSIST, CascadeType.REFRESH])
    @JoinColumn(name = "exchange_id")
    val exchange: StockExchange,

    @ManyToOne(cascade = [CascadeType.PERSIST, CascadeType.REFRESH])
    @JoinColumn(name = "stock_id")
    val stock: Stock,

    @Column(nullable = false)
    val registeredAt: LocalDateTime
    ) {
}