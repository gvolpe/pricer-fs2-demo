package com.gvolpe.fs2.streams.service

import cats.effect.IO
import com.gvolpe.fs2.streams.model.Order

object PricerService {

  private val MarginProfit = 1.05

  def updatePrices(order: Order): IO[Order] = IO {
    val updatedItems = order.items.map(i => i.copy(price = i.price * MarginProfit))
    order.copy(items = updatedItems)
  }

}
