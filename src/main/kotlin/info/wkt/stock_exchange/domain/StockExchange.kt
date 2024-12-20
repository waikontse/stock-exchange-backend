package info.wkt.stock_exchange.domain

import jakarta.persistence.*

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
    val liveInMarket: Boolean = false,

    @OneToMany(mappedBy = "exchange")
    val registrations: Set<StockRegistration>? = null,
) {
}