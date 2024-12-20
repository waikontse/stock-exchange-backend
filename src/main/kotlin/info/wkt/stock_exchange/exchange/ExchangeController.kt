package info.wkt.stock_exchange.exchange

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/stock-exchanges")
@Validated
class ExchangeController(private val exchangeService: ExchangeService) {

    @GetMapping
    fun getAllExchanges(): List<StockExchangeDTO> {
        return exchangeService.getAllExchanges()
    }

    @GetMapping("/{name}")
    fun getExchangeByName(@PathVariable("name") @NotBlank name: String): StockExchangeDTO {
        return exchangeService.getExchangeByName(name)
    }

    @PostMapping
    fun createStockExchange(@Valid @RequestBody command: CreateExchangeCommand): StockExchangeDTO {
        return exchangeService.createExchange(command)
    }

    @PostMapping("/{name}")
    fun updateStockExchange(@PathVariable("name") @NotBlank name: String, @Valid @RequestBody command: AddStockCommand): StockExchangeDTO {
        return exchangeService.addStockToExchange(command)
    }
}

data class CreateExchangeCommand(@NotBlank private val name: String,
                                 @NotBlank val description: String) {
    val upperCaseName: String = name.uppercase()
}

data class AddStockCommand(@NotBlank val exchangeName: String,
                           @NotBlank val stockName: String)