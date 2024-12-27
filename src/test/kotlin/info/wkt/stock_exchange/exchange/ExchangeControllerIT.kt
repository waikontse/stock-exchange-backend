package info.wkt.stock_exchange.exchange

import info.wkt.stock_exchange.stock.CreateStockCommand
import info.wkt.stock_exchange.stock.StockDTO
import info.wkt.stock_exchange.stock.StockRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExchangeControllerIT(
    @Autowired val restTemplate: TestRestTemplate,
    @Autowired val exchangeService: ExchangeService,
    @Autowired val stocks: StockRepository,
    @Autowired val exchanges: StockExchangeRepository,
    @Autowired val registrations: StockRegistrationRepository
) {
    companion object {
        val EXCHANGES_URL = "/api/v1/stock-exchanges"
        val STOCKS_URL = "/api/v1/stocks"
        val EXCHANGES_NAME_URL = "/api/v1/stock-exchanges/%s"
    }

    @BeforeEach
    fun setUp() {
        registrations.deleteAll()
        stocks.deleteAll()
        exchanges.deleteAll()
    }

    @Test
    fun `get all existing exchanges`() {
        // GIVEN
        val command = CreateExchangeCommand("NYSE-2", "expected NYSE description")
        restTemplate.postForEntity<StockExchangeDTO>(EXCHANGES_URL, command)

        // WHEN
        val result = restTemplate.getForEntity<Array<StockExchangeDTO>>(EXCHANGES_URL)

        // THEN
        assertThat(result.body).isNotEmpty
    }

    @Test
    fun `create a non-existing exchange, should be successful`() {
        // GIVEN
        val expectedName = "NYSE"
        val expectedDescription = "NYSE"
        val command = CreateExchangeCommand(expectedName, expectedDescription)
        val exchanges = exchangeService.getAllExchanges().size

        // WHEN
        val result = restTemplate.postForEntity<StockExchangeDTO>(EXCHANGES_URL, command)

        // THEN
        assertAll(
            { assertThat(result.statusCode).isEqualTo(HttpStatus.OK) },
            { assertThat(result.body).isNotNull },
            { assertThat(result.body!!.name).isEqualTo(expectedName) },
            { assertThat(result.body!!.description).isEqualTo(expectedDescription) },
            { assertThat(result.body!!.liveInMarket).isFalse() },
            { assertThat(exchangeService.getAllExchanges().size).isEqualTo(exchanges.inc()) }
        )
    }

    @Test
    fun `create a non-existing exchange with invalid data, should fail`() {
        // GIVEN
        val command = CreateExchangeCommand("", "expected NYSE description")

        // WHEN
        val result = restTemplate.postForEntity<Any>(EXCHANGES_URL, command)

        // THEN
        assertThat(result.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `add non-existing stock to exchange, should be successful`() {
        // GIVEN
        val exchangeCommand = CreateExchangeCommand("AEX", "expected AEX description")
        val stockCommand = CreateStockCommand("VWOO", "VWOO description", 124)
        val addStockCommand = AddStockCommand("AEX", "VWOO")

        // WHEN
        val resultExchange = restTemplate.postForEntity<StockExchangeDTO>(EXCHANGES_URL, exchangeCommand)
        val resultStock = restTemplate.postForEntity<StockDTO>(STOCKS_URL, stockCommand)
        val resultAddStock =
            restTemplate.postForEntity<StockExchangeDTO>(EXCHANGES_NAME_URL.format("AEX"), addStockCommand)

        // THEN
        assertAll(
            { assertThat(resultExchange.statusCode).isEqualTo(HttpStatus.OK) },
            { assertThat(resultStock.statusCode).isEqualTo(HttpStatus.OK) },
            { assertThat(resultAddStock.statusCode).isEqualTo(HttpStatus.OK) },
            { assertThat(resultAddStock.body?.stocks?.size).isEqualTo(1) },
        )
    }

    @Test
    fun `adding existing stock to exchange, should fail`() {
        // GIVEN
        val exchangeCommand = CreateExchangeCommand("AEX", "expected AEX description")
        val stockCommand = CreateStockCommand("VWOO", "VWOO description", 124)
        val addStockCommand = AddStockCommand("AEX", "VWOO")

        // WHEN
        val resultExchange = restTemplate.postForEntity<StockExchangeDTO>(EXCHANGES_URL, exchangeCommand)
        val resultStock = restTemplate.postForEntity<StockDTO>(STOCKS_URL, stockCommand)
        restTemplate.postForEntity<StockExchangeDTO>(EXCHANGES_NAME_URL.format("AEX"), addStockCommand)
        val resultAddStock =
            restTemplate.postForEntity<StockExchangeDTO>(EXCHANGES_NAME_URL.format("AEX"), addStockCommand)

        // THEN
        assertAll(
            { assertThat(resultExchange.statusCode).isEqualTo(HttpStatus.OK) },
            { assertThat(resultStock.statusCode).isEqualTo(HttpStatus.OK) },
            { assertThat(resultAddStock.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY) },
            { assertThat(exchangeService.getExchangeByName(resultExchange.body!!.name).liveInMarket).isFalse() },
            { assertThat(exchangeService.getExchangeByName(resultExchange.body!!.name).stocks).size().isEqualTo(1) },
        )
    }

    @Test
    fun `add stocks to exchange till it's live in the market`() {
        // GIVEN
        val exchangeCommand = CreateExchangeCommand("AEX", "expected AEX description")
        val resultExchange = restTemplate.postForEntity<StockExchangeDTO>(EXCHANGES_URL, exchangeCommand)

        for (i in 1..5) {
            val stockCommand = CreateStockCommand("VWOO.$i", "VWOO.$i description", 124)
            val addStockCommand = AddStockCommand("AEX", "VWOO.$i")

            restTemplate.postForEntity<StockDTO>(STOCKS_URL, stockCommand)
            restTemplate.postForEntity<StockExchangeDTO>(EXCHANGES_NAME_URL.format("AEX"), addStockCommand)
        }

        // WHEN
        val result = restTemplate.getForEntity<StockExchangeDTO>(EXCHANGES_NAME_URL.format("AEX"))

        // THEN
        assertAll(
            { assertThat(resultExchange.statusCode).isEqualTo(HttpStatus.OK) },
            { assertThat(result.body!!.liveInMarket).isTrue() },
            { assertThat(result.body!!.stocks).size().isEqualTo(5) },
        )
    }
}