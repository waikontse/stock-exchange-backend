package info.wkt.stock_exchange.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity(name = "stock")
class Stock(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column
    val name: String,

    @Column
    val description: String?,

    @Column(name = "price_in_cents")
    var currentPrice: Int,

    @Column
    var lastUpdate: LocalDateTime,

    @OneToMany(mappedBy = "stock", cascade = [CascadeType.REFRESH, CascadeType.PERSIST])
    val registrations: Set<StockRegistration> = setOf(),
) {
}