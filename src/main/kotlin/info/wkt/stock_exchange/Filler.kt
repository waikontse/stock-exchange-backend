package info.wkt.stock_exchange

import info.wkt.stock_exchange.exchange.AddStockCommand
import info.wkt.stock_exchange.exchange.CreateExchangeCommand
import info.wkt.stock_exchange.exchange.ExchangeService
import info.wkt.stock_exchange.stock.CreateStockCommand
import info.wkt.stock_exchange.stock.StockService
import org.springframework.boot.CommandLineRunner

//@Component
class Filler(private val stockService: StockService,
             private val stockExchangeService: ExchangeService): CommandLineRunner {
    override fun run(vararg args: String?) {
        // Lets crete a few stock exchanges
        val exchange1 = CreateExchangeCommand("NYSE", "NYSE description")
        val exchange2 = CreateExchangeCommand("AMSXE", "AMSXE description")
        with(stockExchangeService) {
            createExchange(exchange1)
            createExchange(exchange2)
        }


        // Let's create a few stocks
        val stock1 = CreateStockCommand("st1", "st1 description", 100)
        val stock2 = CreateStockCommand("st2", "st2 description", 200)
        val stock3 = CreateStockCommand("st3", "st3 description", 300)
        val stock4 = CreateStockCommand("st4", "st4 description", 400)
        val stock5 = CreateStockCommand("st5", "st5 description", 500)
        val stock6 = CreateStockCommand("st6", "st6 description", 600)

        with(stockService) {
            createNewStock(stock1)
            createNewStock(stock2)
            createNewStock(stock3)
            createNewStock(stock4)
            createNewStock(stock5)
            createNewStock(stock6)
        }

        // Lets register some stocks to exchanges
        with(stockExchangeService) {
            addStockToExchange(AddStockCommand("NYSE", "ST1"))
            addStockToExchange(AddStockCommand("NYSE", "ST2"))
            addStockToExchange(AddStockCommand("NYSE", "ST3"))
            addStockToExchange(AddStockCommand("NYSE", "ST4"))
            addStockToExchange(AddStockCommand("NYSE", "ST5"))

            addStockToExchange(AddStockCommand("AMSXE", "ST2"))
            addStockToExchange(AddStockCommand("AMSXE", "ST4"))
        }

    }
}