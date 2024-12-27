package info.wkt.stock_exchange.exchange

import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/stock-exchanges")
@Validated
class ExchangeController(private val exchangeService: ExchangeService) {

    @Operation(summary = "Get all the available exchanges.")
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllExchanges(): List<StockExchangeDTO> {
        return exchangeService.getAllExchanges()
    }

    @Operation(summary = "Get an exchange based on the exchange name.")
    @GetMapping("/{name}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getExchangeByName(@PathVariable("name") @NotBlank name: String): StockExchangeDTO {
        return exchangeService.getExchangeByName(name)
    }

    @Operation(summary = "Create a new exchange.")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createStockExchange(@Valid @RequestBody command: CreateExchangeCommand): StockExchangeDTO {
        return exchangeService.createExchange(command)
    }

    @Operation(summary = "Add a stock to an exchange.")
    @PostMapping(
        "/{name}",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updateStockExchange(
        @PathVariable("name") @NotBlank name: String,
        @Valid @RequestBody command: AddStockCommand
    ): StockExchangeDTO {
        return exchangeService.addStockToExchange(command)
    }
}

data class CreateExchangeCommand(
    @field:NotBlank val name: String,
    @field:NotBlank val description: String
) {
    fun uppercaseName(): String = name.uppercase()
}

data class AddStockCommand(
    @field:NotBlank val exchangeName: String,
    @field:NotBlank val stockName: String
)