package info.wkt.stock_exchange.stock

import info.wkt.stock_exchange.domain.Stock
import info.wkt.stock_exchange.exceptions.StockAlreadyExistsException
import info.wkt.stock_exchange.exceptions.StockNotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.KeyValuePair
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
@Transactional
class StockService(private val stocks: StockRepository) {
    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    fun createNewStock(command: CreateStockCommand): StockDTO {
        log.info("Creating new stock with name. {}", KeyValuePair("stock", command))
        checkStockDoesNotExist(command)

        val newStock = Stock(
            name = command.upppercaseName(),
            description = command.description,
            currentPrice = command.currentPriceInCents,
            lastUpdate = LocalDateTime.now(ZoneOffset.UTC),
        )

        return stocks.save(newStock).let { StockDTO.fromEntity(it) }
    }

    fun updateStock(command: UpdateStockCommand): StockDTO {
        log.info("Updating stock with name. {}", KeyValuePair("stock", command))

        val stockToUpdate = findByName(command.uppercaseName())
        stockToUpdate.currentPrice = command.currentPriceInCents
        stockToUpdate.lastUpdate = LocalDateTime.now(ZoneOffset.UTC)

        return stocks.save(stockToUpdate).let { StockDTO.fromEntity(it) }
    }

    fun getAllStocks(): List<StockDTO> {
        return stocks.findAll().map { StockDTO.fromEntity(it) }
    }

    fun findByName(stockName: String): Stock {
        return stocks.findByName(stockName)
            ?: throw StockNotFoundException("Stock with name $stockName not found")
    }

    private fun checkStockDoesNotExist(command: CreateStockCommand) {
        val stockAlreadyExists = stocks.existsByName(command.upppercaseName())
        if (stockAlreadyExists) {
            throw StockAlreadyExistsException("Stock with name ${command.upppercaseName()} already exists")
        }
    }
}

data class StockDTO(val name: String, val priceInCents: Int, val description: String, val lastUpdated: LocalDateTime) {
    companion object {
        fun fromEntity(entity: Stock): StockDTO {
            return StockDTO(
                name = entity.name,
                priceInCents = entity.currentPrice,
                description = entity.description ?: "",
                lastUpdated = entity.lastUpdate
            )
        }
    }
}