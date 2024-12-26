package info.wkt.stock_exchange.exceptions

open class BusinessExceptions(open val msg: String): RuntimeException(msg) {
}

class StockAlreadyExistsException(override val msg: String): BusinessExceptions(msg) {
}

class StockAlreadyRegistered(override val msg: String): BusinessExceptions(msg) {}

class ExchangeAlreadyExistsException(override val msg: String): BusinessExceptions(msg) {
}

class ExchangeNotFoundException(override val msg: String): BusinessExceptions(msg) {
}

class StockNotFoundException(override val msg: String): BusinessExceptions(msg) {}