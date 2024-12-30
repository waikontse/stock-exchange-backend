package info.wkt.stock_exchange.exchange

import info.wkt.stock_exchange.domain.StockExchange
import info.wkt.stock_exchange.domain.StockRegistration
import info.wkt.stock_exchange.exceptions.ExchangeAlreadyExistsException
import info.wkt.stock_exchange.exceptions.ExchangeNotFoundException
import info.wkt.stock_exchange.exceptions.StockAlreadyRegistered
import info.wkt.stock_exchange.stock.StockDTO
import info.wkt.stock_exchange.stock.StockService
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
@Transactional
class ExchangeService(
    private val exchanges: StockExchangeRepository,
    private val stocks: StockService,
    private val registrations: StockRegistrationRepository,
    @field:PersistenceContext
    private val entityManager: EntityManager
) {
    companion object {
        val log: Logger = LoggerFactory.getLogger(ExchangeService::class.java)
    }

    fun getAllExchanges(): List<StockExchangeDTO> {
        return exchanges.findAll()
            .map { StockExchangeDTO.fromEntity(it) }
    }

    fun getExchangeByName(name: String): StockExchangeDTO {
        return exchanges.findByName(name)?.let { StockExchangeDTO.fromEntity(it) }
            ?: throw ExchangeNotFoundException("Exchange with name $name not found")
    }

    private fun findExchangeByName(name: String): StockExchange {
        return exchanges.findByName(name) ?: throw ExchangeNotFoundException("Exchange with name $name not found")
    }

    fun createExchange(command: CreateExchangeCommand): StockExchangeDTO {
        log.info("creating an exchange: $command")
        checkExchangeDoesNotExist(command)

        val exchange = StockExchange(
            name = command.uppercaseName(),
            description = command.description,
            liveInMarket = false,
        )

        return exchanges.save(exchange).let { StockExchangeDTO.fromEntity(it) }
    }

    fun addStockToExchange(command: AddStockCommand): StockExchangeDTO {
        log.info("adding stock to exchange: $command")

        val foundExchange = findExchangeByName(command.exchangeName.uppercase())
        val foundStock = stocks.findByName(command.stockName.uppercase())

        if (foundExchange.hasRegisteredStock(foundStock)) {
            throw StockAlreadyRegistered("Stock with name: ${foundStock.name} already registered on exchange: $command.exchangeName")
        }

        val registration = StockRegistration(
            exchange = foundExchange, stock = foundStock, registeredAt = LocalDateTime.now(ZoneOffset.UTC)
        )
        registrations.saveAndFlush(registration)

        updateIsLiveInMarketAndSave(foundExchange)

        return updateIsLiveInMarketAndSave(foundExchange).run { StockExchangeDTO.fromEntity(this) }
    }

    private fun updateIsLiveInMarketAndSave(foundExchange: StockExchange): StockExchange {
        entityManager.refresh(foundExchange)
        foundExchange.updateIsLiveInMarket()

        return exchanges.save(foundExchange)
    }

    private fun checkExchangeDoesNotExist(command: CreateExchangeCommand) {
        val exchangeExists = exchanges.existsByName(command.uppercaseName())
        if (exchangeExists) {
            throw ExchangeAlreadyExistsException("Exchange with name ${command.uppercaseName()} already exists.")
        }
    }
}

data class StockExchangeDTO(
    val name: String,
    val description: String,
    val stocks: List<StockDTO>,
    val liveInMarket: Boolean = false
) {
    companion object {
        fun fromEntity(entity: StockExchange): StockExchangeDTO {
            return StockExchangeDTO(
                entity.name,
                entity.description,
                entity.registrations.map { StockDTO.fromEntity(it.stock) },
                entity.liveInMarket
            )
        }
    }
}