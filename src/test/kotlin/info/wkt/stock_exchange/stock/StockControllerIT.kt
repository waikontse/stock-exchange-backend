package info.wkt.stock_exchange.stock

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForObject
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StockControllerIT(
    @Autowired val restTemplate: TestRestTemplate,
    @Autowired val stocksService: StockService,
) {
    companion object {
        val STOCK_URL = "/api/v1/stocks"
    }

    @Test
    fun `get all stocks`() {
        // GIVEN
        val stocksSize = stocksService.getAllStocks().size

        // WHEN
        val command1 = CreateStockCommand("VWR", "description 1", 200)
        val command2 = CreateStockCommand("VWL", "description 2", 300)
        restTemplate.postForEntity<StockDTO>(STOCK_URL, command1)
        restTemplate.postForEntity<StockDTO>(STOCK_URL, command2)
        val result = restTemplate.getForObject<Array<StockDTO>>(STOCK_URL)

        // THEN
        assertThat(result).hasSize(stocksSize.inc())
    }

    @Test
    fun `add a non-existant stock, should add a new stock`() {
        // GIVEN
        val expectedName = "vwl"
        val expectedDescription = "vwl description"
        val expectedPrice = 100
        val command = CreateStockCommand(expectedName, expectedDescription, expectedPrice)
        assertThat(stocksService.getAllStocks()).hasSize(0)

        // WHEN
        val result = restTemplate.postForEntity<StockDTO>(STOCK_URL, command)

        // THEN
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body?.name ?: "").isEqualTo(expectedName.uppercase())
        assertThat(result.body?.description ?: "").isEqualTo(expectedDescription)
        assertThat(result.body?.priceInCents ?: -1).isEqualTo(expectedPrice)
        assertThat(stocksService.getAllStocks()).hasSize(1)
    }

    @Test
    fun `add stock with non-valid body, should return error`() {
        // GIVEN
        val command = CreateStockCommand("", "description", 100)
        assertThat(stocksService.getAllStocks()).hasSize(0)

        // WHEN
        val result = restTemplate.postForEntity<Any>(STOCK_URL, command)

        // THEN
        assertThat(result.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `add an existing stock, should return error`() {
        // GIVEN
        val expectedName = "vwl"
        val expectedDescription = "vwl description"
        val expectedPrice = 100
        val command = CreateStockCommand(expectedName, expectedDescription, expectedPrice)
        val stocksSize = stocksService.getAllStocks().size

        // WHEN
        restTemplate.postForEntity<StockDTO>(STOCK_URL, command)
        val result = restTemplate.postForEntity<StockDTO>(STOCK_URL, command)

        // THEN
        assertThat(result.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
        assertThat(stocksService.getAllStocks()).hasSize(stocksSize)
    }
}