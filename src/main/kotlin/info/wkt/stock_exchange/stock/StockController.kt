package info.wkt.stock_exchange.stock

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/stocks")
@Validated
class StockController(private val service: StockService) {

    @Operation(summary = "Get all the available stocks.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200"),
    ])
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllStocks(): List<StockDTO> {
        return service.getAllStocks()
    }

    @Operation(summary = "Create a new stock based on the stock name.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200"),
    ])
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createNewStock(@Valid @RequestBody command: CreateStockCommand): StockDTO {
        return service.createNewStock(command)
    }
}

data class CreateStockCommand(@field:NotBlank @field:Size(min = 1, max = 10) val name: String,
                              @field:NotBlank val description: String,
                              @field:Positive @NotNull val currentPriceInCents: Int) {
    fun upppercaseName(): String = this.name.uppercase()
}