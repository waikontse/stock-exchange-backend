package info.wkt.stock_exchange.stock

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/stocks")
class StockController(private val service: StockService) {

    @GetMapping
    fun getAllStocks(): List<StockDTO> {
        return service.getAllStocks()
    }

    @PostMapping
    fun createNewStock(@Valid @RequestBody command: CreateStockCommand): StockDTO {
        return service.createNewStock(command)
    }
}

data class CreateStockCommand(@NotBlank private val name: String,
                              @NotBlank val description: String,
                              @Positive val currentPriceInCents: Int) {
    val upperCasedName: String = name.uppercase()
}